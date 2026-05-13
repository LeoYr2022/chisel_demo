// 与 src/main/scala/Mul34.scala 中时序 `Mul34` 等价（3 拍完成，`start` 当拍锁存）。
// 亦可本地安装 firtool 后运行：`sbt "runMain GenMul34Verilog"` 由 Chisel 自动生成。
module Mul34(
  input  wire clock,
  input  wire reset,
  input  wire       io_start,
  input  wire [2:0] io_a,
  input  wire [3:0] io_b,
  output wire [6:0] io_prod,
  output reg        io_valid
);

  reg        busy;
  reg [1:0]  cnt;
  reg [2:0]  aReg;
  reg [3:0]  bReg;
  reg [6:0]  acc;
  reg [6:0]  prodR;

  assign io_prod = prodR;

  // partial(i): (aReg << i) 零扩展到 7 bit，再按 bReg[i] 选通
  wire [6:0] p1 = bReg[1] ? {3'd0, aReg, 1'b0} : 7'd0;
  wire [6:0] p2 = bReg[2] ? {2'd0, aReg, 2'b0} : 7'd0;
  wire [6:0] p3 = bReg[3] ? {1'b0, aReg, 3'b0} : 7'd0;

  always @(posedge clock) begin
    if (reset) begin
      busy     <= 1'b0;
      cnt      <= 2'd0;
      io_valid <= 1'b0;
    end else begin
      io_valid <= 1'b0;
      if (io_start && !busy) begin
        aReg <= io_a;
        bReg <= io_b;
        acc  <= io_b[0] ? {4'd0, io_a} : 7'd0;
        cnt  <= 2'd1;
        busy <= 1'b1;
      end else if (busy) begin
        if (cnt == 2'd1) begin
          acc <= acc + p1;
          cnt <= 2'd2;
        end else if (cnt == 2'd2) begin
          acc    <= acc + p2 + p3;
          prodR  <= acc + p2 + p3;
          io_valid <= 1'b1;
          busy   <= 1'b0;
        end
      end
    end
  end
endmodule
