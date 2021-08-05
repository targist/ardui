import { useEffect, useRef, useState } from "react";
import { io } from "socket.io-client";

export default function useLogs() {
  const [logs, setLogs] = useState<any[]>([]);
  const [connected, setConnected] = useState(false);
  const emit = useRef<((ev: any, data: any) => void) | null>(null);

  useEffect(() => {
    const socket = io("localhost:8081");
    socket.on("connect", () => {
      setConnected(true);
      console.log("Connected to socket");
    });
    socket.on("disconnect", () => {
      setConnected(false);
      console.log("Disconnected from socket");
    });

    socket.on("logs", (allLogs: any) => {
      if (allLogs instanceof Array) setLogs(allLogs);
      else setLogs([allLogs]);
    });

    emit.current = (ev: any, data: any) => {
      socket.emit(ev, data);
    };
  }, []);

  const commands = {
    upload: (data: any) => emit.current && emit.current("upload", data),
  };

  return {
    connected,
    logs,
    commands,
  };
}
