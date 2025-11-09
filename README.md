# PartyGames

Projekt je **TUI multiplayer aplikacija**, inspirirana od strani Hypixel Party Games.  
Aplikacija temelji na **WebSocket** in **Lanterna** ter ponuja splošno arhitekturo za enostavno dodajanje novih iger.

---

## Table of Contents

1. [Overview](#overview)  
2. [Adding a New Game](#adding-a-new-game)  
   - [Server Side](#server-side)  
   - [Client Side](#client-side)  

---

## Overview

Vse kar potrebujete, je implementacija dveh razredov: enega na **Server** strani in enega na **Client** strani.  

---

## Adding a New Game

### Server Side

- Package: `Server:org.PartyGames.Games`  
- Razred mora extendati **GameStrategy** abstract class in implementirati:

  - Constructor matching super + Optional dodatni parametri  
  - `handleGame()`  
  - `getGame()`

- Novo igro registrirajte v:  
  `GameFactory.createGame()`  

- Za testiranje lahko prilagodite metodo `getRandomGame()`.  
- Skupne komponente so v: `Common:org.PartyGames.Shared.Games`

---

### Client Side

- Package: `Client:org.PartyGames.GameHandlers`  
- Razred mora extendati **GameHandler** abstract class in implementirati:

  - Constructor matching super + Optional dodatni parametri  
  - `handleGame(List<NetworkMessage>)`  
  - `getGame()`

- Novo igro registrirajte v:  
  `GameHandlerFactory.createGameGameHandler()`

---

Ta arhitektura omogoča enostavno razširjanje sistema, zagotavlja pravilno sinhronizacijo med **Server** in **Client** ter ohranja modularnost celotnega projekta.
