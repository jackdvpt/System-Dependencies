System Dependencies
====
Computer components have dependencies which are frequently shared by multiple components.
Removing components which are no longer required is also a necessity. The program itself automates the process of adding and removing components, where components are either installed explicity or implicity (from a different program). Likewise components which arent explicitly installed cannot be exli

The program also detects circular dependencies and will terminate if a dependency is detected, any subsequent DEPEND commands are followed by a warning and all other commands are ehcoed but no other actions are taken in response  runNCommandswCheck will run the check

runNCommandswCheckRecLarge & runNCommandswCheckRecSmall will both produce a warning to the user about the dependency which should be delted to remove a circular dependency

The input file contains a sequence of commands. The commands are ;
- DEPEND item1 item2 [item3 ...] | item1 depends on item 2( and item3...)
- INSTALL item1  | install item1 and those which it depends
- REMOVE item1  | remove item1, and those on which it depends, if possible
- LIST  | list the names of all currently-installed components


Sample Input
----
````
DEPEND TELNET TCPIP NETCARD
DEPEND TCPIP NETCARD
DEPEND DNS TCPIP NETCARD
DEPEND BROWSER TCPIP HTML
INSTALL NETCARD
INSTALL TELNET
INSTALL foo
REMOVE NETCARD
INSTALL BROWSER
INSTALL DNS
LIST
REMOVE TELNET
REMOVE NETCARD
REMOVE DNS
REMOVE NETCARD
INSTALL NETCARD
REMOVE TCPIP
REMOVE BROWSER
REMOVE TCPIP
END
````

Sample Output
----
````
DEPEND TELNET TCPIP NETCARD
DEPEND TCPIP NETCARD
DEPEND DNS TCPIP NETCARD
DEPEND BROWSER TCPIP HTML
INSTALL NETCARD
  Installing NETCARD
INSTALL TELNET
  Installing TCPIP
  Installing TELNET
INSTALL foo
  Installing foo
REMOVE NETCARD
  NETCARD is still needed
INSTALL BROWSER
  Installing HTML
  Installing BROWSER
INSTALL DNS
  Installing DNS
LIST
  NETCARD
  TCPIP
  TELNET
  foo
  HTML
  BROWSER
  DNS
REMOVE TELNET
  Removing TELNET
REMOVE NETCARD
  NETCARD is still needed
REMOVE DNS
  Removing DNS
REMOVE NETCARD
  NETCARD is still needed
INSTALL NETCARD
  NETCARD is already installed
REMOVE TCPIP
  TCPIP is still needed
REMOVE BROWSER
  Removing BROWSER
  Removing HTML
  Removing TCPIP
REMOVE TCPIP
  TCPIP is not installed
END
````
- DEPEND TELNET TCPIP NETCARD
- DEPEND TCPIP NETCARD
- DEPEND DNS TCPIP NETCARD
- DEPEND BROWSER TCPIP HTML
- INSTALL NETCARD
  - Installing NETCARD
- INSTALL TELNET
  - Installing TCPIP
  - Installing TELNET
- INSTALL foo
  - Installing foo
- REMOVE NETCARD
  - NETCARD is still needed
- INSTALL BROWSER
  - Installing HTML
  - Installing BROWSER
- INSTALL DNS
  - Installing DNS
- LIST
  - NETCARD
  - TCPIP
  - TELNET
  - foo
  - HTML
  - BROWSER
  - DNS
- REMOVE TELNET
  - Removing TELNET
- REMOVE NETCARD
  - NETCARD is still needed
- REMOVE DNS
  - Removing DNS
- REMOVE NETCARD
  - NETCARD is still needed
- INSTALL NETCARD
  - NETCARD is already installed
- REMOVE TCPIP
  - TCPIP is still needed
- REMOVE BROWSER
  - Removing BROWSER
  - Removing HTML
  - Removing TCPIP
- REMOVE TCPIP
  - TCPIP is not installed
- END
````
