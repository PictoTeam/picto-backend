# Picto

## Przebieg gry

1. Wgranie zasobów (obrazki, symbole)
    - `POST /symbols`
    - `POST /images`
2. Stworzenie przez administratora reużywalnego konfigu gry
    - `POST /game-configs`
3. Stworzenie sesji na podstawie wybranego configu
    - `POST /sessions/{gameConfigId}`
    - Zwraca kod sesji
4. Dołączenie do gry używając kodu sesji
   - Nawiązanie socketu
5. Rozpoczęcie gry przez administratora
    - `POST /sessions/{sessionId}/start`
6. Główna pętla gry (websocket)
    - Matchmaker dostaje info, że może dobierać pary
    - Kiedy para jest found → emit zmiany czynności
    - Powinien przejść cykl aktywności:
      - Initial mówca
        - action
        - wait (poczekaj na odpowiedź)
        - informacja zwrotna
        - wróć do puli
      - Initial listener
        - wait (poczekaj aż opponent wybierze)
        - answer
        - informacja zwrotna
        - wróć do puli
      - zapisz rundę (async, bo serwer się zatrzyma)
      - przekazanie pary z oznaczeniem, że się widzieli, do MMa
7. Zakończenie gry [#10](https://github.com/PictoTeam/picto-backend/issues/10)
    - Matchmaker przestaje dobierać pary
    - Emituje koniec gry graczom dodawanym do puli
    - Gdy wszyscy gracze są w puli, gra się kończy
8. Podsumowanie [#9](https://github.com/PictoTeam/picto-backend/issues/9)
    - Wygenerowanie podsumowania gry
    - `GET /sessions/{sessionId}/summary`

**TODO:**
- Centralny error handler
- Matchmaker:
  1. podejście iteracyjne w ramach stage’y danych rund
  2. freeze obecnego stanu w ramach pauzy
  3. zatrzymanie tworzenia się nowych rund
- Serwis do websocketów z funkcjami manipulującymi graczem
  1. heartbeat (5 sec z timeoutem):
     - **1.** heartbeat (usuwać z puli, jeżeli się w niej znajduje)  
     - **2.** heartbeat (mogą być problemy partnera)  
     - **3–5.** heartbeat (próbuje przywrócić twojego partnera)  
     - **6.** heartbeat (przerwanie rundy)
- Ponowny rejoin do gry
  1. w trakcie rundy
     - [ ] heartbeat > 6 → wróć do puli MM, szukać nowego & cancel poprzedniej rundy (wyczyszczenie rundy z pamięci)
     - [ ] heartbeat <= 6 → runda/MM będzie znać stany, więc płynnie powracamy do etapu/iteracji, w której był
  2. w trakcie czekania na parę
     - [ ] podejście z heartbeata de facto
