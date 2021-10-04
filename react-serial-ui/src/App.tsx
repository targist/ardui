import React, { useCallback, useEffect, useState } from 'react'
import './App.css'
import useLogs, { Port } from './hooks/useLogs'
import { Button, Container, Grid, makeStyles, Paper, Typography } from '@material-ui/core';

import Editor from "@monaco-editor/react";
import ChangePort from './components/ChangePort';

const useStyles = makeStyles(theme => ({
  container: {
    marginTop: 10,
    // height: "100vh"
  },
  setup: {
    height: "45vh",
    "& > .paper": {
      height: "100%"
    },
  },
  loop: {
    "& > .paper": {
      height: "100%"
    },
    height: "45vh"
  },
  buttons: {
    display: "flex",
    justifyContent: "flex-end",
    border: "none",
    paddingInline: theme.spacing(1),
    marginBlock: 0
  },
  logs: {
    height: "35vh",
    cursor: "text",
    "& > h2": {
      marginLeft: theme.spacing(1),
    },
    "& > .paper": {
      height: "100%",
      padding: theme.spacing(2),
      fontFamily: 'Menlo, Monaco, "Courier New", monospace',
      overflow: "scroll"
    },
  },
  uploadButton: {
    color: theme.palette.info.main,
    marginInline: theme.spacing(1)
  },
  errorMessage: {
    float: "right"
  },
  title: {
    textAlign: "center",
    margin: theme.spacing(1),
  }
}))

function App() {
  const classes = useStyles();
  const [setup, setSetup] = useState(`pinmode 13 output\n`);
  const [loop, setLoop] = useState(`digitalwrite 13 low
sleep 1000
digitalwrite 13 high
sleep 1000\n`);
  const { connected, logs, commands, selectedPort, availablePorts } = useLogs();


  const changeSelectedPort = useCallback(
    (port: Port | null) => {
      if (port && port.path)
        commands.sendSelectPortReq(port.path);
    },
    [commands],
  )

  useEffect(() => {
    if (connected && !selectedPort) {
      const arduinoPort = availablePorts.find(({ manufacturer }) => {
        return manufacturer && (manufacturer || "").toLowerCase().includes("arduino");
      });

      if (arduinoPort) {
        changeSelectedPort(arduinoPort);
      }
    }
  }, [connected, availablePorts])

  const handleUpload = () => {
    commands.upload({
      setup, loop
    });
  }

  return (
    <Container maxWidth="md">
      <Grid
        className={classes.container}
        container
        alignItems="stretch"
        alignContent="space-between"
        spacing={2}>
        <Grid className={classes.setup} item xs={12} md={6}>
          <Paper className="paper" variant="outlined" >
            <Typography className={classes.title} variant="h6" component="h2">
              Setup
            </Typography>
            <Editor
              onChange={(value?: string) => setSetup(value || "")}
              height="80%"
              // defaultLanguage="c"
              defaultLanguage="plaintext"
              // defaultValue="// setup script"
              defaultValue={setup}
            />
          </Paper>
        </Grid>
        <Grid className={classes.loop} item xs={12} md={6}>
          <Paper className="paper" variant="outlined" >
            <Typography className={classes.title} variant="h6" component="h2">
              Loop
            </Typography>
            <Editor
              onChange={(value?: string) => setLoop(value || "")}
              height="80%"
              // defaultLanguage="c"
              defaultLanguage="plaintext"
              // defaultValue="// loop script"
              defaultValue={loop}
            />
          </Paper>
        </Grid>

        <Grid item xs={12}>
          <Paper className={classes.buttons} variant="outlined" >
            {
              availablePorts.length > 1
              && <ChangePort availablePorts={availablePorts} selectedPort={selectedPort} setSelectedPort={port => changeSelectedPort(port)} />
            }
            <Button onClick={handleUpload} variant="outlined" className={classes.uploadButton} disabled={!connected} >
              Upload
            </Button>
          </Paper>
        </Grid>

        <Grid className={classes.logs} item xs={12}>
          <Typography variant="h6" component="h2">
            Logs
          </Typography>
          <Paper className="paper" variant="outlined" >
            {/* TODO: Add auto scroll */}
            {logs.map((log, i) => <div key={`key-${i}`} >
              <Typography variant="body2" >
                {log}
              </Typography>
            </div>)}
          </Paper>
          {connected && availablePorts
            ? (
              selectedPort && <Typography className={classes.errorMessage} variant="caption" color="inherit" >Port: {selectedPort}</Typography>
            ) :
            (
              <Typography className={classes.errorMessage} variant="caption" color="error" >Server disconnected</Typography>
            )}
        </Grid>
      </Grid>
    </Container>
  )
}

export default App
