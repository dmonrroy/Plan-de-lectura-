#!/usr/bin/env python3
"""Genera un plan de lectura canónico de 90 días que cubre los 1.189 capítulos
de la Biblia (66 libros), equilibrado por número estimado de versículos.

El peso de cada capítulo se estima como (versículos del libro / capítulos del
libro). Para el producto final conviene sustituirlo por el conteo real de
versículos por capítulo de la traducción licenciada.

Salida: data/plan_canonico_90.json y docs/plan-canonico-90-dias.md
"""
import json
from pathlib import Path

DIAS = 90
# (id, nombre, capítulos, versículos)
LIBROS = [
    ("GEN","Génesis",50,1533),("EXO","Éxodo",40,1213),("LEV","Levítico",27,859),
    ("NUM","Números",36,1288),("DEU","Deuteronomio",34,959),("JOS","Josué",24,658),
    ("JDG","Jueces",21,618),("RUT","Rut",4,85),("1SA","1 Samuel",31,810),
    ("2SA","2 Samuel",24,695),("1KI","1 Reyes",22,816),("2KI","2 Reyes",25,719),
    ("1CH","1 Crónicas",29,942),("2CH","2 Crónicas",36,822),("EZR","Esdras",10,280),
    ("NEH","Nehemías",13,406),("EST","Ester",10,167),("JOB","Job",42,1070),
    ("PSA","Salmos",150,2461),("PRO","Proverbios",31,915),("ECC","Eclesiastés",12,222),
    ("SNG","Cantares",8,117),("ISA","Isaías",66,1292),("JER","Jeremías",52,1364),
    ("LAM","Lamentaciones",5,154),("EZK","Ezequiel",48,1273),("DAN","Daniel",12,357),
    ("HOS","Oseas",14,197),("JOL","Joel",3,73),("AMO","Amós",9,146),
    ("OBA","Abdías",1,21),("JON","Jonás",4,48),("MIC","Miqueas",7,105),
    ("NAM","Nahúm",3,47),("HAB","Habacuc",3,56),("ZEP","Sofonías",3,53),
    ("HAG","Hageo",2,38),("ZEC","Zacarías",14,211),("MAL","Malaquías",4,55),
    ("MAT","Mateo",28,1071),("MRK","Marcos",16,678),("LUK","Lucas",24,1151),
    ("JHN","Juan",21,879),("ACT","Hechos",28,1007),("ROM","Romanos",16,433),
    ("1CO","1 Corintios",16,437),("2CO","2 Corintios",13,257),("GAL","Gálatas",6,149),
    ("EPH","Efesios",6,155),("PHP","Filipenses",4,104),("COL","Colosenses",4,95),
    ("1TH","1 Tesalonicenses",5,89),("2TH","2 Tesalonicenses",3,47),
    ("1TI","1 Timoteo",6,113),("2TI","2 Timoteo",4,83),("TIT","Tito",3,46),
    ("PHM","Filemón",1,25),("HEB","Hebreos",13,303),("JAS","Santiago",5,108),
    ("1PE","1 Pedro",5,105),("2PE","2 Pedro",3,61),("1JN","1 Juan",5,105),
    ("2JN","2 Juan",1,13),("3JN","3 Juan",1,14),("JUD","Judas",1,25),
    ("REV","Apocalipsis",22,404),
]

def capitulos():
    for lid, nombre, caps, vers in LIBROS:
        for c in range(1, caps + 1):
            yield lid, nombre, c, vers / caps

def rangos(caps):
    """Agrupa capítulos consecutivos del mismo libro: 'Génesis 1–14'."""
    out = []
    for lid, nombre, c, _ in caps:
        if out and out[-1]["libro"] == lid and out[-1]["hasta"] == c - 1:
            out[-1]["hasta"] = c
        else:
            out.append({"libro": lid, "nombre": nombre, "desde": c, "hasta": c})
    return out

def etiqueta(r):
    if r["desde"] == r["hasta"]:
        return f'{r["nombre"]} {r["desde"]}'
    return f'{r["nombre"]} {r["desde"]}–{r["hasta"]}'

def main():
    caps = list(capitulos())
    total_v = sum(p for *_, p in caps)
    assert len(caps) == 1189, len(caps)
    dias, actual, acumulado = [], [], 0.0
    for i, cap in enumerate(caps):
        actual.append(cap)
        acumulado += cap[3]
        restantes_caps = len(caps) - i - 1
        restantes_dias = DIAS - len(dias) - 1
        meta = total_v * (len(dias) + 1) / DIAS
        # Cerrar el día al alcanzar la meta acumulada, garantizando que
        # quede al menos un capítulo para cada día restante.
        if (acumulado >= meta and restantes_dias > 0) or restantes_caps == restantes_dias:
            if restantes_dias >= 0 and len(dias) < DIAS - 1:
                dias.append(actual); actual = []
    if actual:
        dias.append(actual)
    assert len(dias) == DIAS, len(dias)
    assert sum(len(d) for d in dias) == 1189

    plan = []
    for n, d in enumerate(dias, 1):
        plan.append({
            "dia": n,
            "lecturas": rangos(d),
            "capitulos": len(d),
            "versiculos_estimados": round(sum(c[3] for c in d)),
        })
    raiz = Path(__file__).resolve().parent.parent
    (raiz / "data" / "plan_canonico_90.json").write_text(
        json.dumps({"id": "canonico-90", "nombre": "Canónico en 90 días",
                    "total_capitulos": 1189, "dias": plan},
                   ensure_ascii=False, indent=2), encoding="utf-8")

    lineas = [
        "# Plan canónico de 90 días (borrador generado)",
        "",
        "Generado por `tools/generar_plan.py`. Cubre los **66 libros y 1.189 capítulos**, "
        "equilibrado por versículos estimados (≈346 versículos/día, unos 40–50 minutos de lectura "
        "o ~35 minutos en audio). Los días de Salmos agrupan más capítulos porque son cortos.",
        "",
        "| Día | Lectura | Cap. | Vers. aprox. |",
        "|---:|---|---:|---:|",
    ]
    for p in plan:
        lineas.append(f'| {p["dia"]} | {"; ".join(etiqueta(r) for r in p["lecturas"])} '
                      f'| {p["capitulos"]} | {p["versiculos_estimados"]} |')
    (raiz / "docs" / "plan-canonico-90-dias.md").write_text("\n".join(lineas) + "\n", encoding="utf-8")
    vs = [p["versiculos_estimados"] for p in plan]
    print(f"días={len(plan)} total_v={round(total_v)} min={min(vs)} max={max(vs)}")

if __name__ == "__main__":
    main()
