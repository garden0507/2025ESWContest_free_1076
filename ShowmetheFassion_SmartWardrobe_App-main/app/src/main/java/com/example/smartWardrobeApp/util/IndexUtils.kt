package com.example.smartWardrobeApp.util

import com.example.smartWardrobeApp.model.Person

private const val TOTAL_SLOTS = 8

fun remapIndex(oldIndex: Int, selected: Int, total: Int): Int {
    val shift = total - selected
    return ((oldIndex - 1 + shift) % total) + 1
}


fun rebase(people: MutableList<Person>, selected: Int) {
    if (people.isEmpty()) return
    val total = TOTAL_SLOTS
    val rotated = people.map { p -> p.copy(index = remapIndex(p.index, selected, total)) }
    people.clear()
    people.addAll(rotated)
}


fun generateNextIndex(people: List<Person>): Int {
    val used = people.map { it.index }.toSet()
    for (i in 1..TOTAL_SLOTS) {
        if (i !in used) return i
    }
    return -1
}


fun generateNextName(people: List<Person>): String {
    val existing = people.map { it.name }.toSet()
    var n = 1
    var candidate = "clothes$n"
    while (candidate in existing) {
        n++
        candidate = "clothes$n"
    }
    return candidate
}


fun getImageResourceName(person: Person): String {
    val color = person.color.lowercase().replace(" ", "")
    val type = if (person.topOrBottom == "shirt") "tshirt" else "pants"
    val length = if (person.length == "short") "short" else "long"
    return "${color}_${type}_${length}"
}
