import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

class Process {
    public int arrivalTime;
    public int burstTime;
    public int remainingTime;
    public int queueType; // 0 for Queue A, 1 for Queue B
    public int waitTime;
}

public class MultiLevelFeedbackQueue {

    public static void main(String[] args) throws FileNotFoundException {
        // Read the input file
        Scanner scanner = new Scanner(new File("input.txt"));
        ArrayList<Process> processes = new ArrayList<>();
        int clock = 0;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.equals("idle")) {
                clock++;
            } else {
                int burstTime = Integer.parseInt(line);
                Process process = new Process();
                process.arrivalTime = clock;
                process.burstTime = burstTime;
                process.remainingTime = burstTime;
                process.queueType = 0; // all processes start in Queue A
                process.waitTime = 0;
                processes.add(process);
                clock++;
            }
        }
        scanner.close();

        // Implement the two-level feedback queue structure with Queue A and Queue B
        Queue<Process> queueA = new LinkedList<>();
        Queue<Process> queueB = new LinkedList<>();

        // Implement the FCFS Round Robin scheduling algorithm for Queue A with a quantum of 5
        int quantumA = 5;
        int time = 0;
        int dispatchCounter = 0;
        boolean cpuBusy = false;
        Process currentProcess = null;

        // Implement the SJF scheduling algorithm for Queue B with a quantum of 40
        int quantumB = 40;
        int demotionCounter = 0;

        // High-level order of execution for each time tick
        while (!queueA.isEmpty() || !queueB.isEmpty() || cpuBusy) {
            // Check the status of the current process in execution, if any
            if (cpuBusy) {
                currentProcess.remainingTime--;
                if (currentProcess.remainingTime == 0) {
                    currentProcess = null;
                    cpuBusy = false;
                    dispatchCounter++;
                    demotionCounter++;
                    if (demotionCounter == demotionThreshold) {
                        // Demote a process from Queue A to Queue B
                        if (!queueA.isEmpty()) {
                            Process process = queueA.remove();
                            process.queueType = 1;
                            queueB.add(process);
                        }
                        demotionCounter = 0;
                    }
                } else if (currentProcess.queueType == 0 && dispatchCounter == dispatchRatio) {
                    // Dispatch a process from Queue B after every dispatchRatio dispatches from Queue A
                    dispatchCounter = 0;
                    queueA.add(currentProcess);
                    currentProcess = null;
                    cpuBusy = false;
                }
            }

            // Dispatch a process from Queue if CPU is not busy
            if (!cpuBusy) {
                if (!queueA.isEmpty()) {
                    currentProcess = queueA.remove();
                    cpuBusy = true;
                } else if (!queueB.isEmpty()) {
                    currentProcess = queueB.remove();
                    cpuBusy = true;
                }
            }

            // Queue/Fetch Job from the file
            for (Process process : processes) {
                if (process.arrivalTime == time) {
                    if (!cpuBusy) {
                        currentProcess = process;
                        cpuBusy = true;
                    } else {
                        if (process.queueType == 0) {
                            queueA.add(process);
                        } else {
                            queueB.add(process);
                        }
                    }
                }
            }
    
            // Update all times
            for (Process process : queueA) {
                process.waitTime++;
            }
            for (Process process : queueB) {
                process.waitTime++;
            }
            if (cpuBusy) {
                currentProcess.waitTime++;
            }
            time++;
        }
    
        // Calculate metrics
        int totalWaitTime = 0;
        int maxWaitTime = Integer.MIN_VALUE;
        int endTime = time - 1;
        int numProcessesCompleted = 0;
        for (Process process : processes) {
            if (process.remainingTime == 0) {
                numProcessesCompleted++;
                totalWaitTime += process.waitTime;
                if (process.waitTime > maxWaitTime) {
                    maxWaitTime = process.waitTime;
                }
            }
        }
        int totalExecutionTime = time - processes.get(0).arrivalTime;
        int idleTime = totalExecutionTime - endTime;
        double averageWaitTime = (double) totalWaitTime / numProcessesCompleted;
    
        // Output metrics
        System.out.println("---------------------------------------------");
        System.out.println("End Time: " + endTime);
        System.out.println("Processes Completed: " + numProcessesCompleted);
        System.out.println("Total execution time: " + totalExecutionTime);
        System.out.println("Idle time: " + idleTime);
        System.out.println("Longest Wait Time: " + maxWaitTime);
        System.out.println("Average Wait Time: " + averageWaitTime);
        System.out.println("Total Wait Time: " + totalWaitTime);
        System.out.println("---------------------------------------------");
    }
}
    