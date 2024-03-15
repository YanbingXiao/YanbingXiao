

// Car detection module for Lab 1. Determines whether a car
// has either entered or exited the driveway based on the inputs from
// the sensors outer and inner. Inputs are sampled on a clock clk, and
// the system can be reset with a reset input. The module outputs
// an enter or exit output based on whether a car has entered or exited.

// Inputs: 1-bit clk, reset, outer, inner
// Outputs: 1-bit  enter, exit
module car_detection(clk, reset, outer, inner, enter, exit);

	// initializes input and output logic
	input logic clk, reset, outer, inner;
	output logic enter, exit;
	logic new_outer, new_inner; // intermediate logic to handle metastability
	
	// DFF to address metastability
	always_ff @(posedge clk) begin
		new_outer <= outer;
		new_inner <= inner;
	end // always_ff

	enum{S0, S1, S2, S3, S4} ps, ns; // present state, next state
	
	// next state logic 
	always_comb begin
	
		case(ps)

		S0: if ((new_inner & ~new_outer) | (~new_inner & new_outer))  ns = S1;
		    else 			  							       					  ns = S0;

		S1: if (new_inner & new_outer) 			ns = S2;
			 else if (~new_inner & ~new_outer)  ns = S0;
			 else 							         ns = S1;
			 
		S2: if (new_inner & ~new_outer) 		   ns = S3;
			 else if (~new_inner & new_outer)   ns = S4;
			 else 							         ns = S2;
				
		S3: if (~new_outer & ~new_inner)       ns = S0;
			 else 					               ns = S3;
			 
		S4: if (~new_outer & ~new_inner)       ns = S0;
			 else 					 					ns = S4;
			 
		endcase 
		
	end // always_comb
		
	 // sequential logic (DFFs)
	 always_ff @(posedge clk) begin
		 if (reset)
			ps <= S0;
		 else 
			ps <= ns;
	 
	 end //always_ff 
	 
	 // output logic 
	 assign enter = (ps == S3) & (~new_outer & ~new_inner);
	 assign exit = (ps == S4) & (~new_outer & ~new_inner);
 
 endmodule // car_detection
