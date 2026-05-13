// 与 `Adder2`（Chisel RawModule：`sum := a +& b`）等价的 Verilog。
// 若本地已配置 firtool，也可运行：`sbt "runMain GenAdder2Verilog"` 由 Chisel 自动生成 .sv。
module Adder2(
  input  wire [1:0] a,
  input  wire [1:0] b,
  output wire [2:0] sum
);
  assign sum = a + b;
endmodule
