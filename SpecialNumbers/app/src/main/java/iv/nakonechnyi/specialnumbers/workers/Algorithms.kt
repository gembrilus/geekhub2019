package iv.nakonechnyi.specialnumbers.workers

// скачано с интернета
/*fun getPrimes(N: Int): LongArray {
    val sieve = BitSet()
    var x2 = 1L
    var dx2 = 3L
    while (x2 < N) {
        var y2 = 1L
        var dy2 = 3L
        var n: Long
        while (y2 < N) {
            n = (x2 shl 2) + y2
            if (n <= N && (n % 12L == 1L || n % 12L == 5L)) sieve.flip(n.toInt())
            n -= x2
            if (n <= N && n % 12L == 7L) sieve.flip(n.toInt())
            if (x2 > y2) {
                n -= y2 shl 1
                if (n <= N && n % 12L == 11L) sieve.flip(n.toInt())
            }
            y2 += dy2
            dy2 += 2L
        }
        x2 += dx2
        dx2 += 2L
    }

    var r = 5
    var r2 = r * r.toLong()
    var dr2 = (r shl 1) + 1L
    while (r2 < N) {
        if (sieve.get(r)) {
            var mr2 = r2
            while (mr2 < N) {
                sieve.set(mr2.toInt(), false)
                mr2 += r2
            }
        }
        ++r
        r2 += dr2
        dr2 += 2L
    }

    if (N > 2) sieve.set(2, true)
    if (N > 3) sieve.set(3, true)

    val array = mutableListOf<Long>()

    var i = 0
    for (n in 2..N){
        if(sieve[n]) {
            array += n.toLong()
            i++
        }
    }

    return array.toLongArray()
}*/


// мои реализации


fun getPrimes(
    N: Long,
    startNumber: Long,
    list: MutableList<Long>,
    block: (Long) -> Boolean
) {
    var p = startNumber
    if (N >= 2 && p < 3) {
        if (!block(2)) return
    }
    if (p < 3) p = 3
    for (i in p..N step 2) {
        if (list.all { i % it != 0L }) {
            if(!block(i)) return
        }
    }
}


fun fibonacci(N: Long): LongArray {
    val list = mutableListOf<Long>()
    var i = 0L
    while (true) {
        when (i) {
            0L -> list.add(0)
            1L -> list.add(1)
            else -> {
                val next = list[i.toInt() - 1] + list[i.toInt() - 2]
                if (next > N) return list.toLongArray()
                else list.add(next)
            }
        }
        i++
    }
}


fun getArmstrongs(N: Long): LongArray {
    if (N <= 1) return LongArray(0)
    val temp: MutableSet<Long> = sortedSetOf()
    val p = getDigitsCount(N)
    val matrix =
        Array(10) { LongArray(p) }
    for (i in 0..9) {
        for (j in 0 until p) {
            matrix[i][j] =
                pow(i.toLong(), j + 1)
        }
    }
    for (i in 1 until if (N < 10) N else 10) {
        temp.add(i)
    }
    for (i in 3..p) {
        run(0, 1, i, N, temp, matrix)
    }
    val result = LongArray(temp.size)
    var i = 0
    for (l in temp) {
        result[i++] = l
    }
    return result
}

private fun run(
    number: Long,
    first: Int,
    pos: Int,
    N: Long,
    set: MutableSet<Long>,
    m: Array<LongArray>
) {
    for (i in first..9) {
        val n = i * pow(10, pos - 1) + number
        if (n >= N || n < 0) return
        val p = getDigitsCount(n)
        val arm = eval(n, p, m)
        if (getDigitsCount(arm) > p) continue
        if (eval(
                arm,
                getDigitsCount(arm),
                m
            ) == arm && arm < N
        ) {
            set.add(arm)
        }
        if (pos > 1) {
            run(n, i, pos - 1, N, set, m)
        }
    }
}

private fun getDigitsCount(n: Long): Int {
    var n1 = n
    var count = 0
    while (n1 > 0) {
        n1 /= 10
        count++
    }
    return count
}

private fun eval(
    number: Long,
    pow: Int,
    matrix: Array<LongArray>
): Long {
    var num = number
    var temp: Long = 0
    while (num > 0) {
        temp += matrix[(num % 10).toInt()][pow - 1]
        num /= 10
    }
    return temp
}

private fun pow(num: Long, power: Int): Long {
    var temp: Long = 1
    for (i in 0 until power) {
        temp *= num
    }
    return temp
}