
/*
SRRI Scheduler: a while loop within the main
loop executes each task in order using the currentTask variable
1. All tasks are given a number and their function pointers are stored
in the taskScheduler array at their respective indices.
2.The taskState array holds the state of each task (ready,
running, or sleeping)
3.The taskSleep array holds the duration (in ms) each task needs
to sleep for.
4.While tasks are scheduled and are not sleeping, the main while
loop executes them in order.

Task 1: Flash an external LED for 250 ms every 1 second. 
(on for 250ms, off for 750ms).
Task 2: Make the buzzer emit the beginning of the Mario theme song 
*/






#define OUT_PIN PH3 //OUTPUT FOR oc4rA
#define CLK_FREQ 16000000 // Define the clock frequency for timer calculations, 16 MHz for Arduino Mega.
#define CLK_SCALE 64
#define PENDING 0  // Status flag indicating that an ISR (Interrupt Service Routine) cycle is pending.
#define DONE 1     // Status flag indicating that an ISR cycle is complete.
#define RUNNING 0  // Task state indicating it is currently being executed.
#define READY 1    // Task state indicating it is ready to be executed.
#define SLEEP 2    // Task state indicating it is currently sleeping (not executing).
#define N_MAX_TASKS 4 // Maximum number of tasks that can be scheduled in the taskArray.


#define E 659
#define C 523
#define G 784
#define g 392
#define R 0

int song[] = {E, R, E, R, R, E, R, R, C, R, E, R, R, G, R, R, R, R, R, g, R};


int music_count =0;



// Declaration of global arrays and variables for task management.
void (*taskArray[N_MAX_TASKS])(); // Array of function pointers to tasks.
int current_sleeptime[N_MAX_TASKS]; // Array holding the sleep time for each task.
int current_state[N_MAX_TASKS]; // Array indicating the current state of each task.

volatile int sFlag; // Volatile flag used by ISR to signal the main loop about ISR cycle completion.
int task_index; // Index of the current task being processed in the loop.



void setup() {
  OCR1A = CLK_FREQ / (500 * 2 * CLK_SCALE) - 1;
 
  TCCR1B = (1<<WGM12) | (1<<CS11) | (1<<CS10);
  TCCR1B |= (1<< COM1A0) ;// Set Timer 0 to CTC mode with a prescaler of 1024.
  TIMSK1 |= (1<<OCIE1A); // Enable Timer 0 Compare Match A Interrupt.
  
 DDRL |= (1<< PL2);
 DDRH |= (1 << OUT_PIN);

  TCCR4A = 0;
  TCCR4B = 0;
    // set the mode to CTC
    TCCR4B |= (1 << WGM42);
    // set the scaler to be 256
    TCCR4B |= (1 << CS42);
    // set up the toggle mode for output pin
    TCCR4A |= (1<< COM4A0);




 sei(); 
 task_index = 0; // Start processing tasks from the beginning of the taskArray.+++++++++++++++++++++
  sFlag = PENDING; // Initial ISR cycle status set to pending.

  // Setup taskArray with specific tasks, their initial sleep times, and states.
  int j = 0; // Task array index for initialization.
  // task1_on and task2_on are initially set to READY to start execution without delay.
  taskArray[j] = &Task1 ; current_sleeptime[j] = 0; current_state[j] = READY; j++;
  taskArray[j] = &Task2 ; current_sleeptime[j] = 0; current_state[j] = READY; j++;
 
  
  // schedule_sync is always READY to adjust task states and sleep times based on the ISR signal.
  taskArray[j] = &schedule_sync; current_sleeptime[j] = 0; current_state[j] = READY; j++;
  taskArray[j] = NULL; // Mark the end of task initialization with a NULL pointer.

}




ISR(TIMER1_COMPA_vect) {
  sFlag = DONE; 
  TIFR1 |= (1<< OCF1A); // Signal that the ISR cycle is complete, allowing schedule_sync to update tasks.
} 




void loop() {
  // Main loop cycles through tasks, checking their state and executing READY tasks.
  if (current_state[task_index] == READY) {
    current_state[task_index] = RUNNING;
    (*taskArray[task_index])(); // Execute the current READY task.
  }
  task_index++; // Move to the next task in the array.
  // Reset task_index to cycle through tasks continuously.
  if (task_index >= N_MAX_TASKS || taskArray[task_index] == NULL) task_index = 0;

}

void Task1(){
  
  if(millis()% 1000 < 250){
       PORTL |= (1 << PL2);
   }else{    
         
         PORTL &= ~(1 << PL2);
   } 

}

void Task2(){
  if(music_count == sizeof(song)/sizeof(song[0])){
    TCNT4 = 0;
    OCR4A = 0;
    music_count=0;
    sleep_474(8000);
  }else{
     TCNT4 = 0;
     OCR4A = 0;
     set_frequency(song[music_count]);
     music_count++;
     sleep_474(200);
  }


}
 


void sleep_474(int t){
     current_state[task_index] = SLEEP;
     current_sleeptime[task_index] = t;

}


void schedule_sync() {
  // Wait for ISR to signal that the current cycle is done.
  while (sFlag != DONE) {}
  for (int i = 0; taskArray[i] != NULL; i++) {
    // Decrement sleeptime for SLEEPING tasks and update their state to READY if sleeptime has elapsed.
    current_sleeptime[i] -= 2; // Each cycle represents a 2ms decrement.
    if (current_sleeptime[i] <= 0) {
      current_sleeptime[i] = 0;
      current_state[i] = READY; // Task is now READY for execution.
    }
  }
  task_index = -1; // Reset task_index for the next round, ensuring the first task is processed next.
  sFlag = PENDING; // Reset sFlag for the next ISR cycle.
}



void set_frequency(unsigned long frequency) {
    OCR4A = CLK_FREQ / (2 * frequency * 256) - 1;
}












