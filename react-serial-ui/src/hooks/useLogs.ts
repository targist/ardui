import { useEffect, useRef, useState } from "react";
import { io } from "socket.io-client";

export type Port = {
  locationId?: string;
  manufacturer?: string;
  path?: string;
  productId?: string;
  serialNumber?: string;
};

export default function useLogs(port?: string) {
  const [availablePorts, setAvailablePorts] = useState<Port[]>([]);
  const [selectedPort, setSelectedPort] = useState<Port | null>(null);
  const [logs, setLogs] = useState<any[]>([]);
  const [connected, setConnected] = useState(false);
  const emit = useRef<((ev: any, data?: any) => void) | null>(null);

  useEffect(() => {
    const socket = io(location.host);
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

    socket.on("available-ports", (data) => {
      setAvailablePorts(data);
    });

    socket.on("port-selected", (data) => {
      setSelectedPort(data);
    });

    emit.current = (ev: any, data?: any) => {
      data ? socket.emit(ev, data) : socket.emit(ev);
    };
  }, []);

  const commands = {
    upload: (data: any) => emit.current && emit.current("upload", data),
    sendGetAvailablePortsReq: () =>
      emit.current && emit.current("get-available-ports"),
    sendSelectPortReq: (port: string) =>
      emit.current && emit.current("select-port", port),
  };

  useEffect(() => {
    if (connected) {
      commands.sendGetAvailablePortsReq();
    }
  }, [connected]);

  useEffect(() => {
    if (connected && port) {
      commands.sendSelectPortReq(port);
    }
  }, [port]);

  return {
    connected,
    logs,
    commands,
    selectedPort,
    availablePorts,
  };
}
