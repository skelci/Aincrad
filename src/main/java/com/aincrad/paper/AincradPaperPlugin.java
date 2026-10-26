package com.aincrad.paper;

import com.aincrad.paper.command.AincradCommand;
import com.aincrad.paper.listener.GameListener;
import com.aincrad.paper.listener.PlayerListener;
import com.aincrad.paper.persistence.DataStore;
import org.bukkit.plugin.java.JavaPlugin;

public class AincradPaperPlugin extends JavaPlugin implements org.bukkit.event.Listener {

    public java.io.File dataDir() {
        java.io.File dir = new java.io.File("data/Aincrad");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }



    private static AincradPaperPlugin instance;
    private DataStore dataStore;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        this.dataStore = new DataStore(this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new GameListener(this), this);
        mobSpawnService = new com.aincrad.paper.mobs.MobSpawnService(this);
        mobSpawnService.start();
        skillService = new com.aincrad.paper.skills.PaperSkillService(this);
        com.aincrad.paper.progression.ProgressionPersistence.wire(this);
        skillService.start();
        getServer().getPluginManager().registerEvents(new com.aincrad.paper.listener.ItemMechanicListener(), this);
        getServer().getPluginManager().registerEvents(new com.aincrad.paper.listener.DamageNumbers(this), this);
        
        // Generate Floor 1 plaza + override spawn (south of fountain, on road)
        getServer().getScheduler().runTaskLater(this, () -> {
            ensureFloor1();
            for (var world : getServer().getWorlds()) {
                if (world.getName().toLowerCase().contains("floor_")) {
                    configureFloorWorld(world);
                }
                if (isFloor1(world)) {
                    setupFloor1();
                }
            }
        }, 40L);

        getServer().getScheduler().runTaskTimer(this, () -> {
            for (var world : getServer().getWorlds()) {
                String n = world.getName().toLowerCase();
                if (n.contains("floor_") && world.getFullTime() != 12600L) {
                    configureFloorWorld(world);
                }
            }
        }, 200L * 30, 200L * 30);

        registerCommand("skill", (sender, command, label, args) -> {
            if (sender instanceof org.bukkit.entity.Player p) {
                if (args.length >= 1 && args[0].equalsIgnoreCase("drives")) {
                    skillService.giveDrives(p);
                } else {
                    com.aincrad.paper.skills.SkillGui.open(p, skillService);
                }
            } else {
                sender.sendMessage("Player only");
            }
            return true;
        });
        com.aincrad.paper.skills.SkillGui.registerListener(this);
        com.aincrad.paper.quest.QuestManager.getInstance().setMaxActiveQuests(1);
        com.aincrad.paper.world.TownNpcs.register(this);
        com.aincrad.paper.quest.QuestWidget.register(this);
        var skillCmd = getServer().getCommandMap().getCommand("skill");
        if (skillCmd != null) getServer().getCommandMap().register("skills", skillCmd);
        registerCommand("quests", (sender, command, label, args) -> {
            if (sender instanceof org.bukkit.entity.Player p) {
                com.aincrad.paper.quest.QuestGui.open(p);
            } else {
                sender.sendMessage("Player only");
            }
            return true;
        });
        registerCommand("shop", (sender, command, label, args) -> {
            if (sender instanceof org.bukkit.entity.Player p) {
                com.aincrad.paper.skills.ShopGui.open(p);
            } else {
                sender.sendMessage("Player only");
            }
            return true;
        });
        registerCommand("commands", (sender, command, label, args) -> {
            sender.sendMessage("§6§l— Aincrad commands —");
            sender.sendMessage("§e/skills §7— skill menu (rank up, bind drives)");
            sender.sendMessage("§e/stats §7or §e/level §7— level, XP, points");
            sender.sendMessage("§e/shop §7— buy gear/food; §7shift-click own item inside to sell");
            sender.sendMessage("§e/quests §7— quest book (accept / track / turn in)");
            sender.sendMessage("§7NPCs: §eMerchant §7(Market Hall), §eReceptionist §7(Guild Hall)");
            sender.sendMessage("§7Sidebar shows your active quest progress.");
            sender.sendMessage("§7Dungeons: surface huts with gold markers — ladder goes down.");
            sender.sendMessage("§7Skills default keys: F6-F12, -, = (rebind in Controls > Aincrad).");
            return true;
        });
        org.bukkit.command.CommandExecutor stats = (sender, command, label, args) -> {
            if (!(sender instanceof org.bukkit.entity.Player p)) {
                sender.sendMessage("Player only");
                return true;
            }
            var prog = com.aincrad.paper.progression.ProgressionManager.getInstance()
                    .getProgression(p.getUniqueId());
            double cur = com.aincrad.paper.progression.ProgressionManager.xpForLevel(prog.getLevel());
            double next = com.aincrad.paper.progression.ProgressionManager.xpForLevel(prog.getLevel() + 1);
            double have = prog.getXp();
            Double maxHp = com.aincrad.paper.progression.ProgressionManager
                    .currentMaxHealth(p);
            sender.sendMessage("§6§lLevel " + prog.getLevel()
                    + "  §7|  Total XP: §a" + (long) have
                    + "  §7|  Max HP: §c" + (maxHp == null ? "n/a"
                            : String.format(java.util.Locale.ROOT, "%.1f", maxHp))
                    + "  §7|  Skill pts: §b" + prog.getSkillPoints()
                    + "  §7|  Stat pts: §b" + prog.getStatPoints());
            if (prog.isMaxLevel()) {
                sender.sendMessage("§dMax level reached.");
            } else {
                int pct = (int) Math.max(0, Math.min(100, (have - cur) / (next - cur) * 100));
                sender.sendMessage("§7Next level in §e" + (long) (next - have)
                        + " §7XP (§a" + pct + "%§7 of the way)");
            }
            return true;
        };
        getServer().getScheduler().runTaskTimer(this, () -> {
            for (org.bukkit.entity.Player p : getServer().getOnlinePlayers()) {
                com.aincrad.paper.listener.ItemMechanicListener.enforceArmor(p);
            }
        }, 40L, 20L);
        org.bukkit.command.CommandExecutor statsView = (sender, command, label, args) -> {
            if (!(sender instanceof org.bukkit.entity.Player p)) {
                sender.sendMessage("Player only");
                return true;
            }
            int lvl = com.aincrad.paper.progression.ProgressionManager.getInstance()
                    .getProgression(p.getUniqueId()).getLevel();
            Double maxHp = com.aincrad.paper.progression.ProgressionManager.currentMaxHealth(p);
            double def = com.aincrad.paper.listener.ItemMechanicListener.totalDefense(p);
            int str = com.aincrad.paper.progression.ProgressionManager.strengthOf(lvl);
            double mp = com.aincrad.paper.skills.PaperSkillService.mana(p);
            double maxMp = com.aincrad.paper.skills.PaperSkillService.maxMana(p);
            int food = p.getFoodLevel();
            sender.sendMessage("§6§lStats §7(Lv " + lvl + ")");
            sender.sendMessage("§cHP: §f" + String.format(java.util.Locale.ROOT, "%.1f", p.getHealth())
                    + " / " + (maxHp == null ? "n/a" : String.format(java.util.Locale.ROOT, "%.1f", maxHp)));
            sender.sendMessage("§9Defense: §f" + String.format(java.util.Locale.ROOT, "%.1f", def));
            sender.sendMessage("§aStrength: §f" + str);
            sender.sendMessage("§bMP: §f" + String.format(java.util.Locale.ROOT, "%.0f", mp)
                    + " / " + String.format(java.util.Locale.ROOT, "%.0f", maxMp));
            sender.sendMessage("§eST: §f" + food + " / 20");
            return true;
        };
        registerCommand("stats", statsView);
        registerCommand("level", stats);
        registerCommand("aincrad", new com.aincrad.paper.command.AincradCommand(this));
        registerCommand("setlevel", (sender, command, label, args) -> {
            if (!sender.isOp()) {
                sender.sendMessage("No permission");
                return true;
            }
            if (args.length < 2) {
                sender.sendMessage("Usage: /setlevel <player> <level>");
                return true;
            }
            org.bukkit.entity.Player target = getServer().getPlayerExact(args[0]);
            java.util.UUID id = target != null ? target.getUniqueId() : null;
            if (id == null) {
                try {
                    id = java.util.UUID.fromString(args[0]);
                } catch (IllegalArgumentException ex) {
                    sender.sendMessage("Player offline; pass UUID instead: " + args[0]);
                    return true;
                }
            }
            try {
                int lvl = Integer.parseInt(args[1]);
                com.aincrad.paper.progression.ProgressionManager.getInstance().adminSetLevel(id, lvl);
                com.aincrad.paper.progression.ProgressionPersistence.saveAll();
                sender.sendMessage("§aSet " + (target != null ? target.getName() : id) + " to level " + lvl);
            } catch (Exception ex) {
                sender.sendMessage("§c" + ex.getMessage());
            }
            return true;
        });
        com.aincrad.paper.skills.ShopGui.registerListener(this);
        new com.aincrad.paper.mobs.MobFrenzy(this);
        com.aincrad.paper.hud.StatsSync.start(this);
        com.aincrad.paper.world.TreetopCleaner.start(this);
        com.aincrad.paper.quest.QuestGui.register(this);
        getServer().getPluginManager().registerEvents(
                new com.aincrad.paper.listener.TownMenuListener(), this);
        getServer().getScheduler().runTaskTimer(this,
                com.aincrad.paper.quest.QuestWorld::tick, 60L, 40L);
        getServer().getMessenger().registerOutgoingPluginChannel(this, "aincrad:skill_cast");
        getServer().getMessenger().registerOutgoingPluginChannel(this, "aincrad:vfx");
        getServer().getMessenger().registerIncomingPluginChannel(this, "aincrad:skill_cast",
                (channel, player, data) -> {
                    if (data.length < 1) return;
                    int slot = data[0];
                    var def = skillService.boundSkill(player, slot);
                    if (def == null) return;
                    getServer().getScheduler().runTask(this,
                            () -> skillService.use(player, def.id()));
                });

        economyPersistence = new com.aincrad.paper.economy.EconomyPersistence(this);
        economyPersistence.load();
        getServer().getScheduler().runTaskTimer(this,
                () -> economyPersistence.save(), 20L * 60 * 5, 20L * 60 * 5);

        floorBoss = new com.aincrad.paper.world.FloorBoss(this);
        floorBoss.setMobService(mobSpawnService);
        getServer().getPluginManager().registerEvents(floorBoss, this);
        getServer().getScheduler().runTaskLater(this, () -> floorBoss.buildArena(), 100L);

        floorDungeons = new com.aincrad.paper.world.FloorDungeons(this);
        floorDungeons.setMobService(mobSpawnService);
        getServer().getPluginManager().registerEvents(floorDungeons, this);
        getServer().getPluginManager().registerEvents(
                new com.aincrad.paper.listener.FloorWorldListener(), this);
        getServer().getScheduler().runTaskLater(this, () -> floorDungeons.buildAll(), 80L);
        
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("Aincrad Paper plugin enabled.");
    }

    private void ensureFloor1() {
        for (var w : getServer().getWorlds()) {
            if (isFloor1(w)) {
                return;
            }
        }
        try {
            boolean ok = org.bukkit.Bukkit.dispatchCommand(org.bukkit.Bukkit.getConsoleSender(),
                    "execute in aincrad:floor_1 positioned 0.5 66.5 40.5 run forceload add 0 0 0 0");
            getLogger().info("ensureFloor1: lazy dimension load dispatch=" + ok);
        } catch (Throwable t) {
            getLogger().warning("ensureFloor1 failed: " + t);
        }
        getServer().getScheduler().runTaskTimer(this, () -> setupFloor1(), 60L, 100L);
    }

    @org.bukkit.event.EventHandler
    public void onFloorWorldLoad(org.bukkit.event.world.WorldLoadEvent event) {
        if (isFloor1(event.getWorld())) {
            getServer().getScheduler().runTaskLater(this, this::setupFloor1, 40L);
        }
    }

    private boolean floor1Ready = false;

    public void setupFloor1() {
        if (floor1Ready) {
            return;
        }
        org.bukkit.World direct = org.bukkit.Bukkit.getWorld("aincrad:floor_1");
        if (direct == null) {
            direct = org.bukkit.Bukkit.getWorld("world_aincrad_floor_1");
        }
        if (direct != null && isFloor1(direct)) {
            setupFloor1In(direct);
            return;
        }
        for (var world : getServer().getWorlds()) {
            if (isFloor1(world)) {
                setupFloor1In(world);
            }
        }
        if (!floor1Ready && ++floor1Probe % 10 == 1) {
            StringBuilder sb = new StringBuilder();
            for (var w : getServer().getWorlds()) {
                sb.append(w.getName()).append(' ');
            }
            getLogger().info("setupFloor1 probe: direct=" + (direct == null ? "null" : direct.getName())
                    + " worlds=[" + sb + "]");
        }
    }

    private int floor1Probe = 0;

    private void setupFloor1In(org.bukkit.World world) {
        {
            floor1Ready = true;
                com.aincrad.paper.world.PlazaGenerator.generatePlaza(world);
                world.setSpawnLocation(0, 65, 8);
                configureFloorWorld(world);
                if (floorDungeons != null) {
                    floorDungeons.buildAll();
                }
                if (floorBoss != null) {
                    floorBoss.buildArena();
                }
                com.aincrad.paper.world.BureauKiosk.build(world);
            }
    }

    /** SAO floors: eternal dusk, no weather, world border. */
    private void configureFloorWorld(org.bukkit.World world) {
        try {
            world.setFullTime(12600L);
            world.setGameRule(org.bukkit.GameRule.DO_DAYLIGHT_CYCLE, false);
            world.setGameRule(org.bukkit.GameRule.DO_WEATHER_CYCLE, false);
            org.bukkit.GameRule<?> ms = org.bukkit.GameRule.getByName("doMobSpawning");
            if (ms != null) {
                @SuppressWarnings("unchecked")
                org.bukkit.GameRule<Object> gr = (org.bukkit.GameRule<Object>) ms;
                world.setGameRule(gr, Boolean.FALSE);
            }
            world.setStorm(false);
        } catch (Throwable ignored) {
            // custom dimensions may lack an independent world clock
        }
        try {
            org.bukkit.WorldBorder border = world.getWorldBorder();
            border.setCenter(0.5, 0.5);
            border.setSize(isFloor1(world) ? 2800 : 2400);
            border.setWarningDistance(120);
        } catch (Throwable ignored) {
        }
    }

    public static boolean isFloor(int floor, org.bukkit.World world) {
        String n = world.getName().toLowerCase();
        int colon = n.indexOf(':');
        if (colon >= 0) n = n.substring(colon + 1);
        return n.equals("floor_" + floor);
    }

    public static boolean isFloor1(org.bukkit.World world) {
        String n = world.getName().toLowerCase();
        return n.equals("floor_1") || n.endsWith("_floor_1") || n.endsWith(":floor_1");
    }

    public static boolean isFloorWorld(org.bukkit.World world) {
        String n = world.getName().toLowerCase();
        int colon = n.indexOf(':');
        if (colon >= 0) n = n.substring(colon + 1);
        return n.matches("floor_\\d+") || n.contains("aincrad");
    }

    public static AincradPaperPlugin getInstance() {
        return instance;
    }

    private void registerCommand(String name, org.bukkit.command.CommandExecutor executor) {
        var cmd = new org.bukkit.command.Command(name) {
            @Override
            public boolean execute(org.bukkit.command.CommandSender sender, String commandLabel, String[] args) {
                return executor.onCommand(sender, this, commandLabel, args);
            }
            @Override
            public java.util.List<String> tabComplete(org.bukkit.command.CommandSender sender, String alias, String[] args) {
                if (executor instanceof org.bukkit.command.TabCompleter tc) {
                    return tc.onTabComplete(sender, this, alias, args);
                }
                return java.util.List.of();
            }
        };
        getServer().getCommandMap().register(name, cmd);
    }

    @Override
    public void onDisable() {
        if (economyPersistence != null) {
            economyPersistence.save();
        }
        if (skillService != null) {
            skillService.saveAll();
        }
        getLogger().info("Aincrad Paper plugin disabled.");
    }

    private com.aincrad.paper.mobs.MobSpawnService mobSpawnService;
    private com.aincrad.paper.skills.PaperSkillService skillService;

    public com.aincrad.paper.mobs.MobSpawnService getMobSpawnService() { return mobSpawnService; }

    private com.aincrad.paper.world.FloorDungeons floorDungeons;

    public com.aincrad.paper.world.FloorDungeons getFloorDungeons() { return floorDungeons; }
    public com.aincrad.paper.skills.PaperSkillService getSkillService() { return skillService; }
    private com.aincrad.paper.world.FloorBoss floorBoss;
    private com.aincrad.paper.economy.EconomyPersistence economyPersistence;

    public com.aincrad.paper.world.FloorBoss getFloorBoss() { return floorBoss; }

    public String getLastChangeNote() {
        try {
            java.io.File f = new java.io.File(getDataFolder(), "last_change.txt");
            if (!f.exists()) return null;
            return java.nio.file.Files.readString(f.toPath()).trim();
        } catch (Exception e) {
            return null;
        }
    }

    public DataStore getDataStore() {
        return dataStore;
    }
}
