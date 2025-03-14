package com.auo.performancetester.domain.entity

data class BlockStat(
    val readIOs: Long,       // 讀取完成次數
    val readMerges: Long,    // 讀取合併次數
    val readSectors: Long,   // 讀取的區塊數
    val readTimeMs: Long,    // 讀取時間 (ms)
    val writeIOs: Long,      // 寫入完成次數
    val writeMerges: Long,   // 寫入合併次數
    val writeSectors: Long,  // 寫入的區塊數
    val writeTimeMs: Long,   // 寫入時間 (ms)
    val inFlight: Long,      // 進行中的 I/O (通常 USB 為 0)
    val ioTimeMs: Long,      // I/O 操作時間
    val weightedIoTimeMs: Long // 加權 I/O 時間
) {
    companion object {
        fun fromStatLine(line: String): BlockStat {
            val values = line.trim().split("\\s+".toRegex()).map { it.toLong() }
            return BlockStat(
                readIOs = values[0],
                readMerges = values[1],
                readSectors = values[2],
                readTimeMs = values[3],
                writeIOs = values[4],
                writeMerges = values[5],
                writeSectors = values[6],
                writeTimeMs = values[7],
                inFlight = values[8],
                ioTimeMs = values[9],
                weightedIoTimeMs = values[10]
            )
        }

        fun empty() = BlockStat(
            readIOs = 0,
            readMerges = 0,
            readSectors = 0,
            readTimeMs = 0,
            writeIOs = 0,
            writeMerges = 0,
            writeSectors = 0,
            writeTimeMs = 0,
            inFlight = 0,
            ioTimeMs = 0,
            weightedIoTimeMs = 0
        )
    }

    operator fun minus(other: BlockStat): BlockStat = BlockStat(
        readIOs = this.readIOs - other.readIOs,
        readMerges = this.readMerges - other.readMerges,
        readSectors = this.readSectors - other.readSectors,
        readTimeMs = this.readTimeMs - other.readTimeMs,
        writeIOs = this.writeIOs - other.writeIOs,
        writeMerges = this.writeMerges - other.writeMerges,
        writeSectors = this.writeSectors - other.writeSectors,
        writeTimeMs = this.writeTimeMs - other.writeTimeMs,
        inFlight = this.inFlight - other.inFlight,
        ioTimeMs = this.ioTimeMs - other.ioTimeMs,
        weightedIoTimeMs = this.weightedIoTimeMs - other.weightedIoTimeMs
    )

    override fun toString(): String = """
        readIOs=$readIOs, readMerges=$readMerges, readSectors=$readSectors, readTimeMs=$readTimeMs\n
        writeIOs=$writeIOs, writeMerges=$writeMerges, writeSectors=$writeSectors, writeTimeMs=$writeTimeMs\n
        inFlight=$inFlight, ioTimeMs=$ioTimeMs, weightedIoTimeMs=$weightedIoTimeMs"""
}
