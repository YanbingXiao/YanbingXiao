// Lab Members: Anthony Llorico, Yanbing Xiao
// Date: 4/4/2023
// EE 371
// Lab 1: Parking Lot Occupancy Counter

/* testbench for the DE1_SoC */
module DE1_SoC_tb();

	// define signals
	logic	CLOCK_50;
	logic [6:0] HEX0, HEX1, HEX2, HEX3, HEX4, HEX5;
	logic [9:0] LEDR;
	
	// inout pins must be connected to a wire type
	wire [35:0] V_GPIO;
	
	// additional logic required to simulate inout pins
	logic [35:0] V_GPIO_in;
	logic [35:0] V_GPIO_dir; // 1 = input, 0 = output
	
	// set up tristate buffers for inout pins
	genvar i;
	generate
		for (i = 0; i < 36; i++) begin : gpio
			assign V_GPIO[i] = V_GPIO_dir[i] ? V_GPIO_in[i] : 1'bZ;
		end
	endgenerate
	
	// define parameters
	parameter T = 20;
	
	// define simulated clock
	initial begin
		CLOCK_50 <= 0;
		forever	#(T/2)	CLOCK_50 <= ~CLOCK_50;
	end  // initial clock
	
	// instantiate module
	DE1_SoC dut (.*);
	
	// tests various inputs of V_GPIO[23](reset), V_GPIO[24](outer), and V_GPIO[28](inner),
	// which will be connected to switches on a breadboard
	initial begin
	
		// setting directions of pins
		V_GPIO_dir[23] = 1'b1;
		V_GPIO_dir[24] = 1'b1;
		V_GPIO_dir[28] = 1'b1;
		V_GPIO_dir[27] = 1'b0;
		V_GPIO_dir[26] = 1'b0;
	
		// initial reset
		V_GPIO_in[24]<=0; V_GPIO_in[28]<=0;
		V_GPIO_in[23]<=1;           		      @(posedge CLOCK_50);
		V_GPIO_in[23]<=0;           		      @(posedge CLOCK_50);
		
		// incrementing 16 times
		for (int i = 0; i < 16; i++) begin
			V_GPIO_in[24]<=0; V_GPIO_in[28]<=0; @(posedge CLOCK_50);
			V_GPIO_in[24]<=1; V_GPIO_in[28]<=0; @(posedge CLOCK_50);
			V_GPIO_in[24]<=1; V_GPIO_in[28]<=1; @(posedge CLOCK_50);
			V_GPIO_in[24]<=0; V_GPIO_in[28]<=1; @(posedge CLOCK_50);
			V_GPIO_in[24]<=0; V_GPIO_in[28]<=0; @(posedge CLOCK_50);
															@(posedge CLOCK_50);							  
		end
		
			// decrementing 16 times
		for (int i = 0; i < 16; i++) begin
			V_GPIO_in[24]<=0; V_GPIO_in[28]<=0; @(posedge CLOCK_50);
			V_GPIO_in[24]<=0; V_GPIO_in[28]<=1; @(posedge CLOCK_50);
			V_GPIO_in[24]<=1; V_GPIO_in[28]<=1; @(posedge CLOCK_50);
			V_GPIO_in[24]<=1; V_GPIO_in[28]<=0; @(posedge CLOCK_50);
			V_GPIO_in[24]<=0; V_GPIO_in[28]<=0; @(posedge CLOCK_50);
															@(posedge CLOCK_50);							  
		end
		
		// incrementing twice
		V_GPIO_in[24]<=0; V_GPIO_in[28]<=0;    @(posedge CLOCK_50);
		V_GPIO_in[24]<=1; V_GPIO_in[28]<=0;    @(posedge CLOCK_50);
		V_GPIO_in[24]<=1; V_GPIO_in[28]<=1;    @(posedge CLOCK_50);
		V_GPIO_in[24]<=0; V_GPIO_in[28]<=1;    @(posedge CLOCK_50);
		V_GPIO_in[24]<=0; V_GPIO_in[28]<=0;    @(posedge CLOCK_50);
														   @(posedge CLOCK_50);
	   V_GPIO_in[24]<=0; V_GPIO_in[28]<=0;    @(posedge CLOCK_50);
		V_GPIO_in[24]<=1; V_GPIO_in[28]<=0;    @(posedge CLOCK_50);
		V_GPIO_in[24]<=1; V_GPIO_in[28]<=1;    @(posedge CLOCK_50);
		V_GPIO_in[24]<=0; V_GPIO_in[28]<=1;    @(posedge CLOCK_50);
		V_GPIO_in[24]<=0; V_GPIO_in[28]<=0;    @(posedge CLOCK_50);
														   @(posedge CLOCK_50);
								  
	   // reset test
		V_GPIO_in[23]<=1;           				@(posedge CLOCK_50);
		V_GPIO_in[23]<=0;           				@(posedge CLOCK_50);
															@(posedge CLOCK_50);

		$stop; // end simulation
	end // initial
endmodule  // DE1_SoC_tb