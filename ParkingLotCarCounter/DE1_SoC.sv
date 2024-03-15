// Lab Members: Anthony Llorico, Yanbing Xiao
// Date: 4/4/2023
// EE 371
// Lab 1: Parking Lot Occupancy Counter

// This top level module simulates a parking lot counter, which uses two sensors 'outer' and
// 'inner' to detect whether cars enter or exit the parking lot, under the assumption that
// they do not change directions as they enter or exit the lot. 'inner' and 'outer' are simulated
// using switches connected to V_GPIO[28] and V_GPIO[24] respectively. A third switch controls reset,
// which is connected to V_GPIO[23]. The amount of cars present will be displayed on HEXs 0 through 5.
// Inputs are sampled on a clock 'CLOCK_50'

// Inputs: 1-bit CLOCK_50
// Outputs: 6 7-bit HEXs, 10-bit LEDR 
// Inouts: 36-bit V_GPIO
module DE1_SoC (CLOCK_50, HEX0, HEX1, HEX2, HEX3, HEX4, HEX5, LEDR, V_GPIO);

	input  logic		 CLOCK_50;	// 50MHz clock
	output logic [6:0] HEX0, HEX1, HEX2, HEX3, HEX4, HEX5;	// active low
	output logic [9:0] LEDR;
	inout  logic [35:0] V_GPIO;	// expansion header 0 (LabsLand board)
	
	// instantiates the lot_occupancy_counter module with the previously described connections to count and display
	// car count as cars enter and exit
	lot_occupancy_counter counter (.clk(CLOCK_50), .reset(V_GPIO[23]), .inner(V_GPIO[28]), .outer(V_GPIO[24]), 
											 .dis0(HEX0), .dis1(HEX1), .dis2(HEX2), .dis3(HEX3), .dis4(HEX4), .dis5(HEX5));
											 
	// logic assigning LEDS to inner and outer switches to keep track of inputs
	assign V_GPIO[27] = V_GPIO[24]; // outer
	assign V_GPIO[26] = V_GPIO[28]; // inner

endmodule  // DE1_SoC