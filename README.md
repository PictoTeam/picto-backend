# Picto

---

## Zcentralizowany error handler

---

## Stworzenie matchMakera

1. podejście iteracyjne w ramach stage’y danych rund
2. freeze obecnego stanu w ramach pauzy
3. zatrzymanie tworzenia się nowych rund

---

## Serwis do websocketów z funkcjami manipulującymi graczem

### 1. heartbeat (5 sec z timeoutem):

- **1.** heartbeat (usuwać z puli, jeżeli się w niej znajduje)  
- **2.** heartbeat (mogą być problemy partnera)  
- **3–5.** heartbeat (próbuje przywrócić twojego partnera)  
- **6.** heartbeat (przerwanie rundy)

### 2. action

- stwórz konstelację symboli

### 3. answer

- wybierz odpowiedź

### 4. wait

- poczekaj na “słowo” (akcja dla obydwu stron)

### 5. informacja zwrotna

- zgadzają się czy nie?
- może ile punktów?

---

## TODO

### Wgranie zasobów

- [ ] Wgranie obrazków na serwer/dolaczenie ich do kontekstu
- [ ] Wgranie symboli na serwer/dolaczenie ich do kontekstu

### Setupowanie gry 0.1 (przetestować)

- [ ] reużywalność configów

### Dołączenie do gry

- [ ] dobicie się po ID
- [ ] powiązanie z pseudonimem
- [ ] trackowanie wszystkich userów w ramach sesji
- [ ] nawiązanie socketu
- [ ] ustawienie jego stanu na ready/in matchmaker

---

## Przebieg gry ← websocket, główna pętla

### Wystartować grę

- [ ] info dla MM: hej, dobieraj pary
- [ ] kiedy para is found → emit zmiany czynności
- [ ] powinien przejść cykl aktywności:

#### 1. Initial mówca

- [ ] action
- [ ] wait (poczekaj na odpowiedź)
- [ ] informacja zwrotna
- [ ] wróć do puli

#### 2. Initial listener

- [ ] wait (poczekaj aż opponent wybierze)
- [ ] answer
- [ ] informacja zwrotna
- [ ] wróć do puli

- [ ] zapisz rundę (async bo serwer się zatryzma xd)
- [ ] przekazanie pary z oznaczeniem, że się widzieli, do MMa

---

### Ponowny rejoin do gry

#### 1. w trakcie rundy

- [ ] heartbeat > 6 → wróć do puli MM, szukać nowego & cancel poprzedniej rundy (wyczyszczenie rundy z pamięci)
- [ ] heartbeat <= 6 → runda/MM będzie znać stany, więc płynnie powracamy do etapu/iteracji, w której był

#### 2. w trakcie czekania na parę

- [ ] podejście z heartbeata de facto

---

## Zakończenie gry

- [ ] hej MM, nie startuj nowych rund

---

## Optional <Podsumowanie>

- [ ] Szanowny Pan Kosela musi powiedzieć co chce 
