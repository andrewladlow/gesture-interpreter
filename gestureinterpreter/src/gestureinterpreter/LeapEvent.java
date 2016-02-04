package gestureinterpreter;

import javafx.event.Event;
import javafx.event.EventType;

public class LeapEvent extends Event {
	
	private static final long serialVersionUID = 1L;

	public LeapEvent(EventType<? extends Event> arg0) {
        super(arg0);
    }
}
