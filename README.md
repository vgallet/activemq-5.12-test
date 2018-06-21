#Setup Activemq 5.12

Setup permettant de réaliser des tests de haute disponibilité du broker Activemq.

Il permet démarreger deux brokers activemq en mode primaire - secondaire grâce au package master-slave.
L'ensemble est packagé grâce à un docker compose qui contient une base de données PostgreSQL pour réaliser la persistance.
Un serveur haproxy permettant de tester le failover.
Pour lancer ce setup, il suffit de se placer dans le répertoire `master-slave` et de lancer :
`
 docker-compose up --build -d
`
Le dossier `master-master` permet de jouer avec les deux brokers en mode actif actif.


Le dossier `activem-5.12-client` contient diffèrents type de client JMS suivant les besoins:
 - directement connecté au broker
 - avec le support de failover
 - en passant par haproxy
 - failover avec haproxy

