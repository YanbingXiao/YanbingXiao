

// Counts the amount of cars entering or exiting the parking lot with assistance
// from the car_detection module. Operates on a clock clk and can be reset with a 
// reset signal. Uses signals from sensors inner and outer to detect cars and count
// them. The count of these cars is then outputted to outputs dis0-5, which are 7-bit outputs
// intended for use on a 7-seg display. The parking lot is assumed to have a maximum capacity
// of 16 cars.

// Inputs: 1-bit clk, reset, inner, outer
// Outputs: 7-bit dis0, dis1, dis2, dis3, dis4, dis5

module lot_occupancy_counter(clk, reset, inner, outer, 
									  dis0, dis1, dis2, dis3, dis4, dis5);

	// initializes input and output logic
	// and intermediate signals
	input logic clk, reset, inner, outer;
	output logic [6:0] dis0, dis1, dis2, dis3, dis4, dis5;
	logic incr, decr;
	logic [4:0] count;
	
	// instantiates the car_detection module
	car_detection car_detector (.clk(clk), .reset(reset), .outer(outer), .inner(inner), .enter(incr), .exit(decr));
	
	// Car counter logic
	always_ff @(posedge clk) begin
	
		if(reset) 
			count <= 0;
		else if (incr)
			count <= count + 1'b1;
		else if (decr)
			count <= count - 1'b1;
			
	end //always_ff
	
	// display output logic. What is outputted for the 7seg HEXs is determined
	// by car count
	always_comb begin
		
		case(count) 
		
		// 0
		5'b00000: begin  
		  dis5 = 7'b1000110;// C
		  dis4 = 7'b1000111;// L
		  dis3 = 7'b0000110;// E
		  dis2 = 7'b0001000;// A
		  dis1 = 7'b0101111;// R
		  dis0 = 7'b1000000;// 0
	   end
			 
	   // 1
		5'b00001: begin
		  dis5 = 7'b1111111;// off
		  dis4 = 7'b1111111;// off
		  dis3 = 7'b1111111;// off
		  dis2 = 7'b1111111;// off
		  dis1 = 7'b1111111;// off
		  dis0 = 7'b1001111;// 1
		end
			 
		// 2
		5'b00010: begin
		  dis5 = 7'b1111111;// off
		  dis4 = 7'b1111111;// off
		  dis3 = 7'b1111111;// off
		  dis2 = 7'b1111111;// off
		  dis1 = 7'b1111111;// off
		  dis0 = 7'b0100100;// 2
	   end
		 
	   // 3
		5'b00011: begin
		  dis5 = 7'b1111111;// off
		  dis4 = 7'b1111111;// off
		  dis3 = 7'b1111111;// off
		  dis2 = 7'b1111111;// off
		  dis1 = 7'b1111111;// off
		  dis0 = 7'b0110000;// 3
	   end
					 
		// 4
		5'b00100: begin
		  dis5 = 7'b1111111;// off
		  dis4 = 7'b1111111;// off
		  dis3 = 7'b1111111;// off
		  dis2 = 7'b1111111;// off
		  dis1 = 7'b1111111;// off
		  dis0 = 7'b0011001;// 4
	   end
			 
	   // 5
		5'b00101: begin
		  dis5 = 7'b1111111;// off
		  dis4 = 7'b1111111;// off
		  dis3 = 7'b1111111;// off
		  dis2 = 7'b1111111;// off
		  dis1 = 7'b1111111;// off
		  dis0 = 7'b0010010;// 5
	   end
			 
		// 6
		5'b00110: begin
		  dis5 = 7'b1111111;// off
		  dis4 = 7'b1111111;// off
		  dis3 = 7'b1111111;// off
		  dis2 = 7'b1111111;// off
		  dis1 = 7'b1111111;// off
		  dis0 = 7'b0000010;// 6
	   end
			 
	   // 7
		5'b00111: begin
		  dis5 = 7'b1111111;// off
		  dis4 = 7'b1111111;// off
		  dis3 = 7'b1111111;// off
		  dis2 = 7'b1111111;// off
		  dis1 = 7'b1111111;// off
		  dis0 = 7'b1111000;// 7
		end 
			 
	  // 8
		5'b01000: begin
		  dis5 = 7'b1111111;// off
		  dis4 = 7'b1111111;// off
		  dis3 = 7'b1111111;// off
		  dis2 = 7'b1111111;// off
		  dis1 = 7'b1111111;// off
		  dis0 = 7'b0000000;// 8
		end
			 
	   // 9
		5'b01001: begin
		  dis5 = 7'b1111111;// off
		  dis4 = 7'b1111111;// off
		  dis3 = 7'b1111111;// off
		  dis2 = 7'b1111111;// off
		  dis1 = 7'b1111111;// off
		  dis0 = 7'b0011000;// 9
		end
			 
		// 10
		5'b01010: begin
		  dis5 = 7'b1111111;// off
		  dis4 = 7'b1111111;// off
		  dis3 = 7'b1111111;// off
		  dis2 = 7'b1111111;// off
		  dis1 = 7'b1001111;// 1
		  dis0 = 7'b1000000;// 0
		end
			 
	   // 11 
		5'b01011: begin
		  dis5 = 7'b1111111;// off
		  dis4 = 7'b1111111;// off
		  dis3 = 7'b1111111;// off
		  dis2 = 7'b1111111;// off
		  dis1 = 7'b1001111;// 1
		  dis0 = 7'b1001111;// 1
		end
		 
		// 12
		5'b01100: begin
		  dis5 = 7'b1111111;// off
		  dis4 = 7'b1111111;// off
		  dis3 = 7'b1111111;// off
		  dis2 = 7'b1111111;// off
		  dis1 = 7'b1001111;// 1
		  dis0 = 7'b0100100;// 2
		end
			 
	   // 13
		5'b01101: begin
		  dis5 = 7'b1111111;// off
		  dis4 = 7'b1111111;// off
		  dis3 = 7'b1111111;// off
		  dis2 = 7'b1111111;// off
		  dis1 = 7'b1001111;// 1
		  dis0 = 7'b0110000;// 3
		end
			 
		// 14
		5'b01110: begin
		  dis5 = 7'b1111111;// off
		  dis4 = 7'b1111111;// off
		  dis3 = 7'b1111111;// off
		  dis2 = 7'b1111111;// off
		  dis1 = 7'b1001111;// 1
		  dis0 = 7'b0011001;// 4
	   end
			 
	   // 15
		5'b01111: begin
		  dis5 = 7'b1111111;// off
		  dis4 = 7'b1111111;// off
		  dis3 = 7'b1111111;// off
		  dis2 = 7'b1111111;// off
		  dis1 = 7'b1001111;// 1
		  dis0 = 7'b0010010;// 5
		end
			 
		 // 16
		5'b10000: begin
		  dis5 = 7'b0001110;// F
		  dis4 = 7'b1000001;// U
		  dis3 = 7'b1000111;// L
		  dis2 = 7'b1000111;// L
		  dis1 = 7'b1111111;// off
		  dis0 = 7'b1111111;// off
		end
			 
		default: begin  
		  dis5 = 7'b0000110;// E
		  dis4 = 7'b0101111;// R
		  dis3 = 7'b0101111;// R
		  dis2 = 7'b0100011;// O
		  dis1 = 7'b0101111;// R
		  dis0 = 7'b1111111;// off
		end //default		 
		
		endcase
			 
	end //always_comb
	
endmodule //lot_occupancy_counter
