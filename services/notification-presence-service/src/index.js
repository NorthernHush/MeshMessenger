import express from 'express';
import { createServer } from 'http';
import { Server } from 'socket.io';
import Redis from 'ioredis';

const app = express();
const httpServer = createServer(app);

const redisUrl = process.env.REDIS_URL || 'redis://localhost:6379';
const pub = new Redis(redisUrl);
const sub = new Redis(redisUrl);

const io = new Server(httpServer, {
  cors: { origin: '*' }
});

io.adapter(require('socket.io-redis')({ pubClient: pub, subClient: sub }));

io.on('connection', (socket) => {
  console.log('socket connected', socket.id);
  socket.on('presence:update', (data) => {
    // Broadcast presence updates to other sockets
    socket.broadcast.emit('presence:update', data);
  });
});

app.get('/health', (req, res) => res.json({ status: 'ok' }));

const port = process.env.PORT || 4000;
httpServer.listen(port, () => console.log('notify service listening', port));
