# Sandbox ActiveMQ 5.12

Sandbox permettant de réaliser des tests de failover du broker ActiveMQ.

Le dossier *master_slave* permet de démarrer deux brokers ActiveMQ en mode actif - passif.
L'ensemble de la configuration des brokers est modifiable puisqu'il s'agit de répertoires partagés :

 - *conf/master*
 - *conf/slave*
 
Les données et les logs sont disponibles dans le répertoire *conf/data*. La persistance des données peut être réalisée grâce à la base de données fichier KahaDB mais aussi grâce à une base de données PostgreSQL.
Un serveur haproxy est également disponible pour pouvoir tester et visualiser les connexions. 
Le check http des brokers est réalisé grâce à l'API Jolokia :

```
option httpchk GET /api/jolokia HTTP/1.0\r\nAuthorization:\ Basic\ YWRtaW46YWRtaW4=
``` 

Le dossier `master-master` permet de jouer avec les deux brokers en mode actif actif.

Une console [Hawtio](http://hawt.io/) est également disponible pour chaque broker en plus de la console embarquée.

Pour lancer un setup, il suffit de se placer dans le répertoire correspondant et de lancer :

```
 docker-compose up --build -d
```

Le dossier `activem-5.12-client` contient diffèrents type de client JMS Java suivant les besoins:

 - directement connecté au broker
 - avec le support de failover
 - en passant par haproxy
 - failover avec haproxy

