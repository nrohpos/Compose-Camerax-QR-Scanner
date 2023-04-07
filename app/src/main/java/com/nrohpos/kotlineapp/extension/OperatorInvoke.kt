package com.nrohpos.kotlineapp.extension

data class Person(val name: String, val age: Int, val weight: Double)

val personBuilder: (String) -> (Int) -> (Double) -> Person =
    { name ->
        { age ->
            { weight ->
                Person(name, age, weight)
            }
        }
    }

object Console {
    operator fun invoke(block : Test): String {
        return block.kakak()
    }

    operator fun invoke(int: Int): Int {
        return int
    }
}



fun interface Test {
    fun kakak(): String
}