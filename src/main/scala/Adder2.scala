import chisel3._
import circt.stage.ChiselStage

/** 两个 2 bit 无符号数相加；最大 3+3=6，故和为 3 bit（含进位）。 */
class Adder2 extends RawModule {
  val a   = IO(Input(UInt(2.W)))
  val b   = IO(Input(UInt(2.W)))
  val sum = IO(Output(UInt(3.W)))

  sum := a +& b
}

object GenAdder2Verilog extends App {
  // 需要安装 LLVM CIRCT 的 firtool，并在 PATH 中可用。
  // 生成的是 SystemVerilog（.sv）；多数综合器可直接使用。
  ChiselStage.emitSystemVerilogFile(
    new Adder2,
    Array("--target-dir", "verilog_output")
  )
}
