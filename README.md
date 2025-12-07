# PartyGames

PartyGames je TUI (terminal-based) večigralska aplikacija, inspirirana s strani Hypixel Party Games.  
Projekt temelji na WebSocket komunikaciji in knjižnici Lanterna ter ponuja splošno arhitekturo za enostavno dodajanje novih iger.

---

## Overview

Orodje za ustvarjanje Sprite-ov [`Spriter`](https://github.com/MajGucek/Spriter)

Za implementacijo nove igre sta potrebna le dva razreda:

- en razred na Client-side-u
- en razred na Server-side-u

Oba razreda morata implementirati metodo:

```
handleGame(List<NetworkMessage>)
```

Vsa komunikacija, renderiranje terminala in WebSocket infrastruktura sta rešena vnaprej.

---

## Dodajanje nove igre

**Pomembno:**  
Ime Client razreda in Server razreda mora biti enako (npr. `MeteorGame`).

---

## Client-side

<details>
  <summary><strong>Struktura projekta</strong></summary>

  - V paketu `org.PartyGames.Client.Games` se ustvari nov pod-paket z imenom igre (npr. `MeteorGame`).
  - V tem paketu se ustvari en razred, ki razširi `GameClientController`.
</details>

<details>
  <summary><strong>Zahtevane metode</strong></summary>

  **1. Konstruktor**

  Prva vrstica konstruktorja mora biti:
  ```java
  super();
  ```

  **2. handleGame(List<NetworkMessage>)**

  Glavna metoda za izvajanje logike na Client-side-u. Tipična struktura:

  ```java
  @Override
  public void handleGame(List<NetworkMessage> messages) {
      processServerMessages(messages);
      processIO();
      io_handler.render();
  }
  ```

  **Opomba:**  
  Vsi NetworkMessage objekti na Client-side-u so naslovljeni bodisi kot broadcast, bodisi direktno na Client.
</details>

<details>
  <summary><strong>Izbirne metode</strong></summary>

  - Namesto `System.out` se uporablja `Logger` s tremi nivoji (`info`, `warn`, `error`):

    ```java
    private static final Logger logger = LoggerFactory.getLogger(MeteorGame.class);
    logger.info("Entered MeteorGame");
    ```

  - Metodi `start()` in `stop()` se lahko prepišejo, vendar morata vedno najprej poklicati:

    ```java
    @Override
    public void start() {
        super.start();
        logger.info("Entered MeteorGame");
    }
    ```
</details>

<details>
  <summary><strong>Razpoložljeni objekti</strong></summary>

  **io_handler**

  Input:
  ```java
  KeyStroke key = io_handler.poll();
  ```

  Render metode:
  - `clearScreen()`
  - `drawSquare()`
  - `drawText()`
  - `setCursor()`
  - `render()`

  Barve:
  ```java
  getRGB("red");
  getRGB(255, 0, 0);
  ```

  **connection**

  - Glavna metoda za komunikacijo:
    ```java
    connection.send(NetworkMessage message);
    ```
</details>

<details>
  <summary><strong>NetworkMessage</strong></summary>

  Ustvarjanje:
  ```java
  NetworkMessage message = new NetworkMessage();
  message.setMessageType(...).setData(...).setAddress(...);
  ```

  Getterji delujejo enako kot setterji, vendar so polja lahko `null`.

  Builder vzorec:
  https://refactoring.guru/design-patterns/builder/java/example

  Helper razredi so dovoljeni znotraj lastnega paketa.
</details>

---

## Server-side

<details>
  <summary><strong>Struktura projekta</strong></summary>

  - V paketu `org.PartyGames.Server.Games` se ustvari razred, ki razširi `GameServerController`.
  - Ime razreda mora biti enako Client razredu.
</details>

<details>
  <summary><strong>Zahtevane metode</strong></summary>

  **1. Konstruktor**

  Prva vrstica mora biti:
  ```java
  super();
  ```

  **2. handleGame(List<NetworkMessage>)**

  - Sprejme sporočila od vseh Clientov.
  - Glavna logika igre na Server-side-u poteka tukaj.
</details>

<details>
  <summary><strong>Izbirne metode</strong></summary>

  - Priporočljiva uporaba Logger-ja:

    ```java
    private static final Logger logger = LoggerFactory.getLogger(MeteorGame.class);
    ```

  - Metodi `start()` in `stop()` se lahko prepišejo, vendar morata vedno najprej poklicati:

    ```java
    super.start();
    super.stop();
    ```
</details>

<details>
  <summary><strong>Komunikacija s Clienti</strong></summary>

  - Glavna metoda za pošiljanje sporočil vsem Clientom:

    ```java
    connection.notifyClients(NetworkMessage message);
    ```

  **Naslavljanje sporočil:**
  - broadcast:
    ```java
    message.setToBroadcast();
    ```
  - specifičen naslov Clienta:
    ```java
    message.setAddress("ClientName");
    ```

  Če sporočilo ni broadcast in naslov ne ustreza Client-u, ga Client zavrne še preden pride do `handleGame()`.
</details>

---
