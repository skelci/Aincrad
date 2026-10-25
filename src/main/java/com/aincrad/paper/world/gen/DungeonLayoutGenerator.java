package com.aincrad.paper.world.gen;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Grid dungeon layout generator ported from the tested MystAsc
 * UDungeonLayoutGenerator (socket encoding, 90-degree rotation, flush
 * door-to-door adjacency). Pure logic, no Bukkit - unit-testable standalone.
 */
public final class DungeonLayoutGenerator {

    public enum RoomType { ENTRANCE, PASSING, MONSTER, LOOT, MINI_BOSS, STAIRS, BOSS }

    /** Door, 2 cells wide. side: 0=N(z-) 1=E(x+) 2=S(z+) 3=W(x-). */
    public record Door(int side, int start) {}

    public record Template(String id, RoomType type, int w, int h, List<Door> doors, int weight) {}

    public record Placed(Template t, int x, int y, int rot, List<Door> doors) {}

    public record Conn(int a, int b) {}

    public static final class Layout {
        public final int gw, gh;
        public final List<Placed> rooms = new ArrayList<>();
        public final List<Conn> conns = new ArrayList<>();
        Layout(int gw, int gh) { this.gw = gw; this.gh = gh; }

        public Placed find(RoomType type) {
            for (Placed p : rooms) if (p.t.type == type) return p;
            return null;
        }
    }

    public static final List<Template> DEFAULT_POOL = List.of(
            new Template("entrance",   RoomType.ENTRANCE,  3, 2, List.of(new Door(2, 1)), 10),
            new Template("straight",   RoomType.PASSING,   2, 2, List.of(new Door(0, 0), new Door(2, 0)), 30),
            new Template("straight_l", RoomType.PASSING,   2, 3, List.of(new Door(0, 0), new Door(2, 0)), 18),
            new Template("corner",     RoomType.PASSING,   2, 2, List.of(new Door(0, 0), new Door(1, 0)), 22),
            new Template("tee",        RoomType.PASSING,   2, 2, List.of(new Door(0, 0), new Door(1, 0), new Door(2, 0)), 14),
            new Template("cross",      RoomType.PASSING,   2, 2, List.of(new Door(0, 0), new Door(1, 0), new Door(2, 0), new Door(3, 0)), 8),
            new Template("hall_s",     RoomType.MONSTER,   3, 3, List.of(new Door(0, 0), new Door(2, 1)), 20),
            new Template("hall_b",     RoomType.MONSTER,   4, 3, List.of(new Door(0, 1), new Door(2, 1), new Door(3, 0)), 12),
            new Template("mini_boss",  RoomType.MINI_BOSS, 4, 4, List.of(new Door(0, 1), new Door(2, 1)), 10),
            new Template("loot",       RoomType.LOOT,      2, 2, List.of(new Door(1, 0)), 12),
            new Template("stairs",     RoomType.STAIRS,    2, 2, List.of(new Door(0, 0)), 10),
            new Template("boss",       RoomType.BOSS,      4, 4, List.of(new Door(0, 1)), 10));

    private DungeonLayoutGenerator() {}

    /**
     * entranceX >= 0 pins the entrance room to that grid x (needed so the
     * surface shaft can find it). anchorX/anchorY pin the final room
     * (stairs or boss) so levels stack vertically aligned.
     */
    public static Layout generate(long seed, int gw, int gh, List<Template> pool,
                                  boolean entranceLevel, boolean bossLevel,
                                  int entranceX, int anchorX, int anchorY, int middleCount) {
        for (int attempt = 0; attempt < 120; attempt++) {
            int mids = Math.max(4, middleCount - attempt / 8);
            Layout l = attempt(seed + attempt * 7919L, gw, gh, pool, entranceLevel,
                    bossLevel, entranceX, anchorX, anchorY, mids);
            if (l != null) return l;
        }
        throw new IllegalStateException("no valid layout for seed " + seed);
    }

    private static Layout attempt(long seed, int gw, int gh, List<Template> pool,
                                  boolean entranceLevel, boolean bossLevel,
                                  int entranceX, int anchorX, int anchorY, int middleCount) {
        Random rnd = new Random(seed);
        Layout out = new Layout(gw, gh);
        Map<Long, Integer> occ = new HashMap<>();

        if (entranceLevel) {
            Template e = pick(pool, RoomType.ENTRANCE);
            int ex = entranceX >= 0 ? entranceX : rnd.nextInt(Math.max(1, gw - e.w + 1));
            Placed ep = new Placed(e, Math.max(0, Math.min(gw - e.w, ex)), 0, 0, e.doors());
            out.rooms.add(ep);
            mark(ep, occ, 0);
        }

        List<Template> mids = new ArrayList<>();
        for (Template t : pool)
            if (t.type != RoomType.ENTRANCE && t.type != RoomType.BOSS && t.type != RoomType.STAIRS)
                mids.add(t);

        int placed = 0, attempts = 0;
        while (placed < middleCount && attempts++ < 6000) {
            Template t = weighted(mids, rnd);
            int rot = rnd.nextInt(4);
            int[] dim = rotDim(t.w, t.h, rot);
            if (dim[0] > gw || dim[1] > gh) continue;
            Placed c = new Placed(t,
                    rnd.nextInt(gw - dim[0] + 1),
                    rnd.nextInt(gh - dim[1] + 1),
                    rot, rotateDoors(t.doors(), t.w, t.h, rot));
            if (!canPlace(c, gw, gh, occ)) continue;
            if (!out.rooms.isEmpty() && doorConnections(c, out.rooms).isEmpty()) continue;
            out.rooms.add(c);
            mark(c, occ, out.rooms.size() - 1);
            placed++;
        }

        Template fin = pick(pool, bossLevel ? RoomType.BOSS : RoomType.STAIRS);
        try {
            placeFinal(out, occ, fin, anchorX, anchorY, rnd);
        } catch (IllegalStateException e) {
            return null;
        }
        recomputeConns(out);
        if (reachableFromEntrance(out) != out.rooms.size()) return null;
        if (entranceLevel && out.conns.stream().noneMatch(c -> c.a() == 0 || c.b() == 0))
            return null;
        return out;
    }

    /** Pinned last-room placement; relocates until it touches the cluster. */
    private static void placeFinal(Layout out, Map<Long, Integer> occ,
                                   Template fin, int anchorX, int anchorY, Random rnd) {
        int fx = Math.max(0, Math.min(out.gw - fin.w, anchorX));
        int fy = Math.max(0, Math.min(out.gh - fin.h, anchorY));
        Placed fp = new Placed(fin, fx, fy, 0, fin.doors());
        List<int[]> offsets = new ArrayList<>();
        for (int dx = -5; dx <= 5; dx++)
            for (int dy = -5; dy <= 5; dy++)
                offsets.add(new int[] { dx, dy, rnd.nextInt(1000) });
        Comparator<int[]> byDist = Comparator
                .comparingInt((int[] o) -> o[0] * o[0] + o[1] * o[1])
                .thenComparingInt(o -> o[2]);
        offsets.sort(byDist);
        int[] rots = fin.type() == RoomType.STAIRS ? new int[] { 0 } : new int[] { 0, 1, 2, 3 };
        for (int rot : rots) {
            List<Door> doors = rotateDoors(fin.doors(), fin.w, fin.h, rot);
            for (int[] off : offsets) {
                Placed c = new Placed(fin, fx + off[0], fy + off[1], rot, doors);
                if (!canPlace(c, out.gw, out.gh, occ)) continue;
                if (!out.rooms.isEmpty() && doorConnections(c, out.rooms).isEmpty()) continue;
                out.rooms.add(c);
                mark(c, occ, out.rooms.size() - 1);
                return;
            }
        }
        throw new IllegalStateException("no spot for final room");
    }


    /** Pairs of flush, facing doors between candidate and the given rooms. */
    public static List<int[]> doorConnections(Placed c, List<Placed> others) {
        List<int[]> hits = new ArrayList<>();
        for (Placed o : others) {
            if (o == c) continue;
            for (Door cd : c.doors()) {
                int[] ourOuter = doorCells(c, cd, true);
                for (Door od : o.doors()) {
                    int[] theirInner = doorCells(o, od, false);
                    if (theirInner[0] == ourOuter[0] && theirInner[1] == ourOuter[1]
                            && theirInner[2] == ourOuter[2] && theirInner[3] == ourOuter[3]) {
                        hits.add(new int[] { cd.side(), ourOuter[0], ourOuter[1] });
                        break;
                    }
                }
            }
        }
        return hits;
    }

    /**
     * The 2 cells a door occupies, as (minX,minY,maxX,maxY).
     * outside=true shifts one cell off the room (the corridor side).
     */
    public static int[] doorCells(Placed p, Door d, boolean outside) {
        int[] dim = rotDim(p.t.w, p.t.h, p.rot);
        int w = dim[0], h = dim[1];
        int ix, iy, sx = 0, sy = 0;
        switch (d.side()) {
            case 0: ix = p.x + d.start(); iy = p.y; sy = -1; break;
            case 1: ix = p.x + w - 1; iy = p.y + d.start(); sx = 1; break;
            case 2: ix = p.x + d.start(); iy = p.y + h - 1; sy = 1; break;
            default: ix = p.x; iy = p.y + d.start(); sx = -1; break;
        }
        int ax = ix, ay = iy;
        int bx = d.side() % 2 == 0 ? ix + 1 : ix;
        int by = d.side() % 2 == 0 ? iy : iy + 1;
        if (outside) { ax += sx; ay += sy; bx += sx; by += sy; }
        return new int[] { Math.min(ax, bx), Math.min(ay, by), Math.max(ax, bx), Math.max(ay, by) };
    }

    public static void recomputeConns(Layout l) {
        l.conns.clear();
        for (int i = 0; i < l.rooms.size(); i++)
            for (int j = i + 1; j < l.rooms.size(); j++)
                if (!doorConnections(l.rooms.get(i), List.of(l.rooms.get(j))).isEmpty())
                    l.conns.add(new Conn(i, j));
    }


    public static int[] rotDim(int w, int h, int rot) {
        return (rot % 2 == 0) ? new int[] { w, h } : new int[] { h, w };
    }

    static List<Door> rotateDoors(List<Door> doors, int w, int h, int rot) {
        List<Door> cur = new ArrayList<>(doors);
        int cw = w, ch = h;
        for (int r = 0; r < (rot % 4 + 4) % 4; r++) {
            List<Door> next = new ArrayList<>(cur.size());
            for (Door d : cur) {
                int ns = (d.side() + 1) % 4;
                int nstart = (d.side() == 1 || d.side() == 3) ? ch - 2 - d.start() : d.start();
                next.add(new Door(ns, nstart));
            }
            int t = cw; cw = ch; ch = t;
            cur = next;
        }
        return cur;
    }

    public static Integer roomAt(Layout l, int x, int y) {
        for (int i = 0; i < l.rooms.size(); i++) {
            Placed p = l.rooms.get(i);
            int[] dim = rotDim(p.t.w, p.t.h, p.rot);
            if (x >= p.x && x < p.x + dim[0] && y >= p.y && y < p.y + dim[1]) return i;
        }
        return null;
    }

    /** BFS reachability over conns; returns number of rooms reachable from room 0. */
    public static int reachableFromEntrance(Layout l) {
        Set<Integer> seen = new HashSet<>();
        List<Integer> stack = new ArrayList<>();
        stack.add(0);
        seen.add(0);
        while (!stack.isEmpty()) {
            int r = stack.remove(stack.size() - 1);
            for (Conn c : l.conns) {
                int n = c.a() == r ? c.b() : c.b() == r ? c.a() : -1;
                if (n >= 0 && seen.add(n)) stack.add(n);
            }
        }
        return seen.size();
    }

    private static boolean canPlace(Placed p, int gw, int gh, Map<Long, Integer> occ) {
        int[] dim = rotDim(p.t.w, p.t.h, p.rot);
        if (p.x < 0 || p.y < 0 || p.x + dim[0] > gw || p.y + dim[1] > gh) return false;
        for (int x = p.x; x < p.x + dim[0]; x++)
            for (int y = p.y; y < p.y + dim[1]; y++)
                if (occ.containsKey(key(x, y))) return false;
        return true;
    }

    private static void mark(Placed p, Map<Long, Integer> occ, int idx) {
        int[] dim = rotDim(p.t.w, p.t.h, p.rot);
        for (int x = p.x; x < p.x + dim[0]; x++)
            for (int y = p.y; y < p.y + dim[1]; y++)
                occ.put(key(x, y), idx);
    }

    private static void unmark(Placed p, Map<Long, Integer> occ) {
        int[] dim = rotDim(p.t.w, p.t.h, p.rot);
        for (int x = p.x; x < p.x + dim[0]; x++)
            for (int y = p.y; y < p.y + dim[1]; y++)
                occ.remove(key(x, y));
    }

    private static long key(int x, int y) { return ((long) x << 32) ^ (y & 0xffffffffL); }

    private static Template pick(List<Template> pool, RoomType type) {
        for (Template t : pool) if (t.type() == type) return t;
        throw new IllegalArgumentException("pool missing type " + type);
    }

    private static Template weighted(List<Template> pool, Random rnd) {
        int total = 0;
        for (Template t : pool) total += Math.max(1, t.weight());
        int r = rnd.nextInt(total);
        for (Template t : pool) {
            r -= Math.max(1, t.weight());
            if (r < 0) return t;
        }
        return pool.get(pool.size() - 1);
    }

    public static String ascii(Layout l) {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < l.gh; y++) {
            for (int x = 0; x < l.gw; x++) {
                Integer idx = roomAt(l, x, y);
                sb.append(idx == null ? '.' : "EMPBLSX".charAt(l.rooms.get(idx).t.type.ordinal()));
            }
            sb.append('\n');
        }
        return sb.append("rooms=").append(l.rooms.size())
                .append(" conns=").append(l.conns.size()).toString();
    }
}
