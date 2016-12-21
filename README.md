# ![parc logo](https://github.com/Sauuman/parc/blob/master/Parc/app/src/main/res/mipmap-mdpi/ic_launcher.png?raw=true) PARC
PARC (**P**hone **A**s **a** **R**emote **C**ontrol) is an Android application which allows the remote control of a 
computer (OS X only so far) using simple Gesture.

The system is split into two components, an Android application which sends action codes using UDP; and a server on the 
computer which listen for those action codes and execute the matching action.

> **WARNING**: This application sends and receives unencrypted data over UDP without any form of authentication.
>
> This means that anyone aware of the IP address and port number used by the server will be able to send commands
> to the computer as well.
> 
> *This application should only be used over a private secure network for research and testing purposes.
The authors are not liable in case of abuses.*
 
## Authors
 * Jacques Dafflon <jacques.dafflon@usi.ch>
 * Samantha Rosso <samantha.rosso@usi.ch>
 
### Authorship of the various parts
The Android application was written jointly by both authors. Specifically, Jacques Dafflon wrote most of the UDP-related
code while, the main activity, the preferences, and the about and help dialogs were written by Samantha Rosso. The gestures
related code was written by both authors together.
 
The Python server was written by Jacques Dafflon and the images and graphics were made or adapted by Samantha Rosso.
 
## Android application
The android application is located in the `Parc` folder. 

When running the app, make sure to set the IP address of your computer in the settings. You can find the IP address of
your computer (on OS X) by `alt` + clicking on the WiFi icon on the top right of the screen.
Make sure as well that you are on the same network as your computer and that the server is running.

## Server
The server is located in the `server` folder.

The server is written in Python and requires Python `3.5` or greater to run.  Before launching the server for the first
time, make sure you have installed the dependencies using `pip` as follow:
 
 ```bash
 pip3.5 install -r server/requirements.txt
 ```
 
Launching the server can be done simply by running the `parc_server.py` script:
 ```bash
./parc_server.py
```

Use `^C` to stop the server.
By default the server will listen on all available interfaces (`0.0.0.0`). Listening on a specific interface is possible
by specifying the interfaces IPv4 address with the `--ip` flag.

The default port is `8089`, but can be changed using the `--port` flag. (Make sure to also change the port in the 
Android application)

By default the server does not print any information. The `--verbose` flag will tell the server to print the data it
receives.

```
usage: parc_server.py [-h] [-i IP] [-p PORT] [-v]

PARC Server

optional arguments:
  -h, --help            show this help message and exit
  -i IP, --ip IP        IP Address to listen to
  -p PORT, --port PORT  Port to listen on
  -v, --verbose         Show the received data.
```

## Docs
The `docs` folder contains the project initial and final reports as well as the slides from the final presentation.

## License & Credits

The code is open source and available under the MIT license. A copy of the license is available in the `LICENSE` file

The touch gesture icons were created by Jeff Portaro and modified by Samantha Rosso.

The App icon was created by Axis Media and modified by Samantha Rosso.
