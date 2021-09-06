import React from 'react';
import { makeStyles } from '@material-ui/core/styles';
import Button from '@material-ui/core/Button';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemText from '@material-ui/core/ListItemText';
import DialogTitle from '@material-ui/core/DialogTitle';
import Dialog from '@material-ui/core/Dialog';
import Typography from '@material-ui/core/Typography';
import { blue } from '@material-ui/core/colors';
import { Port } from '../hooks/useLogs';


export interface ChangePortDialogProps {
  availablePorts: Port[];
  open: boolean;
  selectedPort: Port | null;
  onClose: (value: Port | null) => void;
}

function ChangePortDialog(props: ChangePortDialogProps) {
  const { onClose, selectedPort, open, availablePorts } = props;

  const handleClose = () => {
    onClose(selectedPort);
  };

  const handleListItemClick = (value: Port) => {
    onClose(value);
  };

  return (
    <Dialog onClose={handleClose} aria-labelledby="simple-dialog-title" open={open}>
      <DialogTitle id="simple-dialog-title">Select Port</DialogTitle>
      <List>
        {availablePorts.map((port) => (
          <ListItem
            button
            onClick={() => handleListItemClick(port)}
            key={port.path}
          >
            <ListItemText primary={port.path} />
          </ListItem>
        ))}
      </List>
    </Dialog>
  );
}
export type ChangePortProps = {
  selectedPort: Port | null;
  setSelectedPort: (port: Port | null) => void;
  availablePorts: Port[];
};
export default function ChangePort({ selectedPort, setSelectedPort, availablePorts }: ChangePortProps) {
  const [open, setOpen] = React.useState(false);

  const handleClickOpen = () => {
    setOpen(true);
  };

  const handleClose = (value: Port | null) => {
    setOpen(false);
    setSelectedPort(value);
  };

  return (
    <>
      <Button style={{
        float: "right",
      }} variant="outlined" color="default" onClick={handleClickOpen}>
        Change Port
      </Button>
      <ChangePortDialog availablePorts={availablePorts} selectedPort={selectedPort} open={open} onClose={handleClose} />
    </>
  );
}