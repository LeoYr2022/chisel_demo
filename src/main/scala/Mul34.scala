import chisel3._
import circt.stage.ChiselStage

/** 3 bit × 4 bit 无符号乘法器（时序）。在 `start` 后连续 3 个时钟周期完成一次运算，完成当拍 `valid` 拉高。 */
class Mul34 extends Module {
  val io = IO(new Bundle {
    val start = Input(Bool())
    val a     = Input(UInt(3.W))
    val b     = Input(UInt(4.W))
    val prod  = Output(UInt(7.W))
    val valid = Output(Bool())
  })

  val busy  = RegInit(false.B)
  val cnt   = RegInit(0.U(2.W))
  val aReg  = Reg(UInt(3.W))
  val bReg  = Reg(UInt(4.W))
  val acc   = Reg(UInt(7.W))
  val prodR = Reg(UInt(7.W))

  def partial(i: Int): UInt =
    Mux(bReg(i), (aReg << i).pad(7), 0.U(7.W))

  io.prod  := prodR
  io.valid := false.B

  when(io.start && !busy) {
    aReg := io.a
    bReg := io.b
    // 与寄存器同拍更新，必须用端口值，不能用尚未更新的 aReg/bReg。
    acc := Mux(io.b(0), (io.a << 0).pad(7), 0.U(7.W))
    cnt   := 1.U
    busy  := true.B
  }.elsewhen(busy) {
    when(cnt === 1.U) {
      acc  := acc + partial(1)
      cnt  := 2.U
    }.elsewhen(cnt === 2.U) {
      val next = acc + partial(2) + partial(3)
      acc   := next
      prodR := next
      io.valid := true.B
      busy  := false.B
    }
  }
}

object GenMul34Verilog extends App {
  // 需要 LLVM CIRCT 的 firtool 在 PATH 中；生成 SystemVerilog 时可用 --emit-verilog 等选项。
  ChiselStage.emitSystemVerilogFile(
    new Mul34,
    Array("--target-dir", "verilog_output")
  )
}
