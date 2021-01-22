# IFT3325_TP2

    - Jessy Grondin (20119453)
    - Benjamin Dagenais (20116871)

Ce TP consiste en l'implémentation simplifiée du protocole HDLC avec le protocole Go-Back-N


## HDLC (High-Level Data Link Control)

- HDLC est une protocole de la couche liaison (niveau 2) du Modèle OSI. 
- Utilisé pour délimiter les trames de différents type en ajoutant un contrôle d'erreur.
- offre une service de transfert de données fiable et efficace entre deux systèmes adjacents

## Go-Back-N

- Protocole de fenêtre glissante permettant à l'émetteur de ne pas attendre pour un acquittement avant d'envoyer un autre frame lorsque la fenêtre n'est pas full.
