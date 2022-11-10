package assignment6;

import org.junit.BeforeClass;
import org.junit.Test;


import assignment6.Flight.SeatClass;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

//import scoreannotation.Score;

public class A6SampleTest {

    private static String flightNo = "EE 422C";
    private static List<Flight.Ticket> concurrencyTestLog;

    private static void joinAllThreads(List<Thread> threads)
            throws InterruptedException {
        for (Thread t : threads) {
            t.join();
        }
    }

    /**
     * Initialize tests for concurrency by simulating BookingClient with
     * 1) TO1: 99 ECONOMY Class Clients
     * 2) TO2: 99 ECONOMY Class Clients
     * 3) Flight: FIRST Class -> 0 rows,
     * 	BUSINESS Class -> 0 rows,
     * 	ECONOMY Class -> 33 rows
     * <p>
     * Stores the transactions into concurrencyTestLog
     *
     * @throws InterruptedException
     */
    @BeforeClass
    public static void setupBeforeClass() throws InterruptedException {
    	final SeatClass[] seatPreferences = new SeatClass[99];
    	Arrays.fill(seatPreferences, SeatClass.ECONOMY);
    	final SeatClass[] seatPreferences2 = Arrays.copyOf(seatPreferences, 99);

        Map<String, SeatClass[]> offices = new HashMap<String, SeatClass[]>() {{
            put("TO1", seatPreferences);
            put("TO2", seatPreferences2);
        }};

        Flight flight = new Flight(flightNo, 0, 0, 33);
        BookingClient bookingClient = new BookingClient(offices, flight);
        joinAllThreads(bookingClient.simulate());

        concurrencyTestLog = flight.getTransactionLog();
    }

    /**
     * Tests whether getNextAvailableSeat() can lower seat class if the preferred one is full
     * <p>
     * Preconditions: 2 rows that are allocated for FIRST class is full,
     * 	Customer asks for a FIRST class seat
     * Expected: 3A (BUSINESS)
     *
     * @throws InterruptedException
     */
    @Test(timeout = 120000)
    //@Score(1)
    public void givenSeatClassIsFullWhenGetNextAvailableSeatThenLowerSeatClass() throws InterruptedException {
    	final SeatClass[] seatPreferences = new SeatClass[4];
    	Arrays.fill(seatPreferences, SeatClass.FIRST);
    	final SeatClass[] seatPreferences2 = Arrays.copyOf(seatPreferences, 4);

        Map<String, SeatClass[]> offices = new HashMap<String, SeatClass[]>() {{
        	put("TO1", seatPreferences);
            put("TO2", seatPreferences2);
        }};

        Flight flight = new Flight(flightNo, 2, 2, 10);
        BookingClient bookingClient = new BookingClient(offices, flight);
        joinAllThreads(bookingClient.simulate());

        Flight.Seat next = flight.getNextAvailableSeat(SeatClass.FIRST);
        assertNotNull(next);
        assertEquals("3A (BUSINESS)", next.toString());
    }

    /**
     * Tests whether getNextAvailableSeat() can handle an empty seat class
     * <p>
     * Preconditions: 2 rows that are allocated for FIRST class is empty,
     * 	Customer asks for a FIRST class seat
     * Expected: 1A (FIRST)
     */
    @Test(timeout = 120000)
    //@Score(1)
    public void givenSeatClassIsEmptyWhenGetNextAvailableSeatThenReturnFirstSeat() {
        Flight flight = new Flight(flightNo, 2, 2, 10);
        Flight.Seat next = flight.getNextAvailableSeat(SeatClass.FIRST);
        assertNotNull(next);
        assertTrue(next.toString().equalsIgnoreCase("1A (FIRST)"));
    }

    /**
     * Checks to see whether some seats fill while others remain empty
     * You should have no output that the flight is full
     */
    @Test(timeout = 120000)
    //@Score(1)
    public void havingSpareSeats() throws InterruptedException {
        final SeatClass[] seatPreferences = new SeatClass[5];
        Arrays.fill(seatPreferences, SeatClass.FIRST);
        final SeatClass[] seatPreferences2 = Arrays.copyOf(seatPreferences, 5);

        Map<String, SeatClass[]> offices = new HashMap<String, SeatClass[]>() {{
            put("TO1", seatPreferences);
            put("TO2", seatPreferences2);
        }};

        Flight flight = new Flight("EE 422C", 1, 1, 10);
        BookingClient bookingClient = new BookingClient(offices, flight);
        joinAllThreads(bookingClient.simulate());

        Flight.Seat next = flight.getNextAvailableSeat(SeatClass.FIRST);
        assertNotNull(next);
        assertEquals("3A (ECONOMY)", next.toString());
    }

    @Test(timeout = 120000)
    //@Score(1)
    public void fillingUpBusinessWithZeroEconomy() throws InterruptedException {
        final SeatClass[] seatPreferences = new SeatClass[5];
        Arrays.fill(seatPreferences, SeatClass.FIRST);
        final SeatClass[] seatPreferences2 = Arrays.copyOf(seatPreferences, 5);
        final SeatClass[] seatPreferences3 = Arrays.copyOf(seatPreferences, 1);
        Arrays.fill(seatPreferences3, SeatClass.ECONOMY);

        Map<String, SeatClass[]> offices = new HashMap<String, SeatClass[]>() {{
            put("TO1", seatPreferences);
            put("TO2", seatPreferences2);
            put("TO3", seatPreferences3);
        }};

        Flight flight = new Flight("EE 422C", 2, 5, 0);
        BookingClient bookingClient = new BookingClient(offices, flight);
        joinAllThreads(bookingClient.simulate());

        Flight.Seat next = flight.getNextAvailableSeat(SeatClass.ECONOMY);;
        assertEquals(null, next);
    }

    @Test(timeout = 120000)
    //@Score(1)
    public void otherClasses() throws InterruptedException {
        final SeatClass[] seatPreferences = new SeatClass[5];
        Arrays.fill(seatPreferences, SeatClass.FIRST);
        final SeatClass[] seatPreferences2 = Arrays.copyOf(seatPreferences, 5);
        final SeatClass[] seatPreferences3 = Arrays.copyOf(seatPreferences, 1);
        Arrays.fill(seatPreferences3, SeatClass.ECONOMY);

        Map<String, SeatClass[]> offices = new HashMap<String, SeatClass[]>() {{
            put("TO1", seatPreferences);
            put("TO2", seatPreferences2);
        }};

        Flight flight = new Flight("EE 422C", 2, 5, 0);
        BookingClient bookingClient = new BookingClient(offices, flight);
        joinAllThreads(bookingClient.simulate());

        Flight.Seat next = flight.getNextAvailableSeat(SeatClass.BUSINESS);;
        assertNotNull(next);
        assertEquals("3C (BUSINESS)", next.toString());
    }

    @Test(timeout = 120000)
    //@Score(1)
    public void onlyEconomy() throws InterruptedException {
        final SeatClass[] seatPreferences = new SeatClass[5];
        Arrays.fill(seatPreferences, SeatClass.FIRST);
        final SeatClass[] seatPreferences2 = Arrays.copyOf(seatPreferences, 5);

        Map<String, SeatClass[]> offices = new HashMap<String, SeatClass[]>() {{
            put("TO1", seatPreferences);
            put("TO2", seatPreferences2);
        }};

        Flight flight = new Flight("EE 422C", 0, 0, 10);
        BookingClient bookingClient = new BookingClient(offices, flight);
        joinAllThreads(bookingClient.simulate());

        Flight.Seat next = flight.getNextAvailableSeat(SeatClass.BUSINESS);;
        assertNotNull(next);
        assertEquals("2E (ECONOMY)", next.toString());
    }

    @Test(timeout = 120000)
    //@Score(1)
    public void onlyEconomyButGetsFull() throws InterruptedException {
        final SeatClass[] seatPreferences = new SeatClass[5];
        Arrays.fill(seatPreferences, SeatClass.FIRST);
        final SeatClass[] seatPreferences2 = Arrays.copyOf(seatPreferences, 5);

        Map<String, SeatClass[]> offices = new HashMap<String, SeatClass[]>() {{
            put("TO1", seatPreferences);
            put("TO2", seatPreferences2);
        }};

        Flight flight = new Flight("EE 422C", 0, 0, 1);
        BookingClient bookingClient = new BookingClient(offices, flight);
        joinAllThreads(bookingClient.simulate());

        Flight.Seat next = flight.getNextAvailableSeat(SeatClass.BUSINESS);;
        assertEquals(null, next);
    }
}