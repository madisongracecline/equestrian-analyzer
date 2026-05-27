# Equestrian Round Analyzer

A Java command-line application that logs showjumping rounds and uses rule-based analysis to identify the probable causes of faults — helping riders and coaches make smarter training decisions.
---
## What It Does

After completing a showjumping round, a rider enters details about the round into the program. The analyzer then produces a fault report with probability-weighted causes and actionable training recommendations.

**The program collects:**
- Rider and horse information
- Competition name and jump class (e.g. 1.30m)
- Environmental conditions (indoor/outdoor, ground condition, wind)
- Warm-up quality
- Rider's mental state (confident, nervous, tired, focused, normal)
- Horse behavioral tendencies (rushing, backing off, spookiness, lazy hind legs)
- Fault details — fence number, fence type, approach pace, rail position (front/hind), combination stride data, related distances
- Time faults

**The program outputs:**
- A summary of all faults with course position percentages
- Probability-weighted causes for each fault (e.g. "82% - Pace too fast on approach [SHARED]")
- Whether the fault is attributed to the RIDER, HORSE, or is SHARED
- Specific training recommendations for each identified cause
- Round history saved to a local file for future reference

---

## Example Output

```
==========================================
      EQUESTRIAN ROUND ANALYZER
   Showjumping Fault Analysis Tool
==========================================

-- FAULTS --
  Rail at fence 6 (Oxer) - 50% through course

-- PROBABLE CAUSES --
  82% - Pace too fast on approach [SHARED]
     -> Work on half-halts 5-6 strides out. Re-establish rhythm.
  75% - Horse lazy with hind legs over oxer [HORSE]
     -> Grid work with bounce fences. Sharpen hind leg response.
```

---

## How to Run

**Requirements:** Java JDK 8 or higher

**Compile:**
```bash
javac EquestrianAnalyzer.java
```

**Run:**
```bash
java EquestrianAnalyzer
```

Round history is automatically saved to `data/rounds.txt` and reloaded each time the program runs.

---

## Key Features

- **Rule-based fault analysis** — probability scores adjust based on horse tendencies, course position, fence type, and rider state
- **Combination fence tracking** — detects incorrect stride counts between combination elements
- **Related distance detection** — flags stride adjustment errors between related fences
- **Persistent round history** — saves all logged rounds to a local file
- **Clean menu-driven interface** — easy to use with no GUI required

---

## Technologies Used

- Java (core library only — no external dependencies)
- File I/O for persistent data storage
- Object-oriented and procedural logic

---

## About

Built as a personal project combining a passion for showjumping with software development. Designed to give riders and coaches a structured, data-driven way to reflect on competition rounds and target training more effectively.
