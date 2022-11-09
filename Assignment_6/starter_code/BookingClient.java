/* MULTITHREADING <BookingClient.java>
 * EE422C Project 6 submission by
 * Replace <...> with your actual data.
 * Arnav Vats
 * AV36676
 * 17610
 * Slip days used: <0>
 * Fall 2022
 */
package assignment6;

import java.util.Map;

import assignment6.Flight.SeatClass;
import assignment6.Flight.Ticket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.lang.Thread;

public class BookingClient {

    Map<String, SeatClass[]> offices;
    Flight flight;

	/**
     * @param offices maps ticket office id to seat class preferences of customers in line
     * @param flight the flight for which tickets are sold for
     */
    public BookingClient(Map<String, SeatClass[]> offices, Flight flight) {
        this.offices = offices;
        this.flight = flight;
    }

    /**
     * Starts the ticket office simulation by creating (and starting) threads
     * for each ticket office to sell tickets for the given flight
     *
     * @return list of threads used in the simulation,
     * should have as many threads as there are ticket offices
     */
    public List<Thread> simulate() {
        List<Thread> threads = new ArrayList<Thread>();
        
        for(String office: offices.keySet()){
            OfficeThreading current = new OfficeThreading(office, offices.get(office), flight); 
            Thread curThread = new Thread(current);
            threads.add(curThread);
        }
        for(Thread thread : threads){
            thread.start();
        }

        return threads;
    }

    public static void main(String[] args) {
        
        Flight testFlight = new Flight("TR123", 1, 1, 1);
        Map<String, SeatClass[]> testMap = new HashMap<String, SeatClass[]>();
        testMap.put("TO1", new SeatClass[] {SeatClass.BUSINESS, SeatClass.BUSINESS, SeatClass.BUSINESS});
        testMap.put("TO3", new SeatClass[] {SeatClass.BUSINESS, SeatClass.ECONOMY, SeatClass.ECONOMY});
        testMap.put("TO2", new SeatClass[] {SeatClass.FIRST, SeatClass.BUSINESS, SeatClass.ECONOMY, SeatClass.ECONOMY});
        testMap.put("TO5", new SeatClass[] {SeatClass.BUSINESS, SeatClass.BUSINESS, SeatClass.BUSINESS});
        testMap.put("TO4", new SeatClass[] {SeatClass.ECONOMY, SeatClass.ECONOMY, SeatClass.ECONOMY});
    
        BookingClient testClient = new BookingClient(testMap, testFlight);
        testClient.simulate();

    }
}

class OfficeThreading implements Runnable { // A class that implements the runnable interface and is used to run the threads
    String office;
    SeatClass[] customers;
    static Object lock = new Object();
    static Object print = new Object();
    Flight flight;
    static Boolean full = false;


    public OfficeThreading(String office, SeatClass[] customers, Flight flight){
        this.office = office;
        this.customers = customers;
        this.flight = flight;
        
        
    }
    @Override
    public void run(){
        for (int i = 0; i < customers.length; i++) {

            synchronized(lock){ //synchronizes all threads so that only one thread is doing work at a time
                if(!full){ // a flag that communicates with all threads and checks whether the flight is full
                    Flight.Seat curSeat = flight.getNextAvailableSeat(customers[i]);
                    if(curSeat == null){ // if the next available seat is null, then the flight is full
                        System.out.println("Sorry, we are sold out!"); //ensures that this statement is only printed once 
                        full = true; //sets the full flag to true
                    }
                    flight.printTicket(office, curSeat, flight.getCustNum()); //prints the ticket

                } 
                else{

                    return; //Once the flight is full, then return from all threads
                
                }
            }
                
                
        }
        
        
    }
}
