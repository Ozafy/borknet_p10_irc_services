# BorkNet P10 IRC Services

A set of IRC Services to complement [QuakeNet's newserv](https://github.com/quakenet/newserv).

## Requirements

- JRE 1.6 or later
- Git
- Basic knowledge of Linux

## Installation

Checkout the repository (do this on the root of a user's home, otherwise adjust the 'services' bash file accordingly)

```
git clone https://github.com/Ozafy/borknet_p10_irc_services.git
```

Now setup the bots (edit **borknet_services/bot.conf** and every .conf file in the **borknet_services/core/modules/\*** directories)

```
host=borkserv.borknet.org
server=127.0.0.1
toport=4400
connectpass=secret
numeric=]O
```

Add a Connect,Port and Uworld block to your snircd config.

```
Connect {
 name = "borkserv.borknet.org";
 host = "127.0.0.1";
 password = "secret";
 port = 4400;
 class = "Server";
 autoconnect = no;
 hub;
};
Port {
 server = yes;
 port = ipv4 4400;
 hidden = yes;
 vhost = "127.0.0.1";
};
# you should add this to your existing UWorld block
UWorld {
 name = "borkserv.borknet.org";
};
```

Finally start the services with

```
chmod u+x services
./services
```

Good luck!