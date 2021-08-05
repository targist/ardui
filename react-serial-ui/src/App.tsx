import React, { useState } from 'react'
import './App.css'
import useLogs from './hooks/useLogs'
import { Button, Container, Grid, makeStyles, Paper, Typography } from '@material-ui/core';

import Editor from "@monaco-editor/react";

const useStyles = makeStyles(theme => ({
  container: {
    marginTop: 10,
    // height: "100vh"
  },
  setup: {
    height: "50vh",
    "& > .paper": {
      height: "100%"
    },
  },
  loop: {
    "& > .paper": {
      height: "100%"
    },
    height: "50vh"
  },
  buttons: {
    display: "flex",
    justifyContent: "flex-end",
    border: "none",
    paddingInline: theme.spacing(1)
  },
  logs: {
    height: "40vh",
    cursor: "text",
    "& > .paper": {
      height: "100%"
    },
  },
  uploadButton: {
    color: theme.palette.info.main
  },
  errorMessage: {
    float: "right"
  }
}))

function App() {
  const classes = useStyles();
  const [setup, setSetup] = useState("")
  const [loop, setLoop] = useState("");
  const { connected, logs, commands } = useLogs();

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
            <Editor
              onChange={(value?: string) => setSetup(value || "")}
              height="100%"
              defaultLanguage="c"
              defaultValue="// setup script"
            />
          </Paper>
        </Grid>
        <Grid className={classes.loop} item xs={12} md={6}>
          <Paper className="paper" variant="outlined" >
            <Editor
              onChange={(value?: string) => setLoop(value || "")}
              height="100%"
              defaultLanguage="c"
              defaultValue="// loop script"
            />
          </Paper>
        </Grid>

        <Grid item xs={12}>
          <Paper className={classes.buttons} variant="outlined" >
            <Button onClick={handleUpload} variant="outlined" className={classes.uploadButton} disabled={!connected} >
              Upload
            </Button>
          </Paper>
        </Grid>

        <Grid className={classes.logs} item xs={12}>
          <Paper className="paper" variant="outlined" >
            <pre>{JSON.stringify(logs, null, 2)}</pre>
          </Paper>

          {(!connected) && <Typography className={classes.errorMessage} variant="caption" color="error" >Server disconnected</Typography>}
        </Grid>
      </Grid>
    </Container>
  )
}

export default App
