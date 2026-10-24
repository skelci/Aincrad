import socket, struct, sys
def pkt(i, t, b): return struct.pack('<ii', i, t) + b + b'\x00\x00'
s = socket.create_connection(('127.0.0.1', 25575), timeout=10)
def send(i, t, b):
    s.sendall(pkt(i, t, b))
    ln, typ = struct.unpack('<ii', s.recv(8))
    data = b''
    while len(data) < ln - 4: data += s.recv(65536)
    return data[4:-2].decode(errors='replace')
send(1, 3, b'AinCrac1Con!')
print(send(2, 2, ' '.join(sys.argv[1:])))
