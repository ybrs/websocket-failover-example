package nl.ybrs.eventserver;


import com.hazelcast.core.EntryEvent;
import com.hazelcast.map.listener.*;

class RoomMapEntryListener implements EntryAddedListener<String, String>,
        EntryRemovedListener<String, String>,
        EntryUpdatedListener<String, String>,
        EntryEvictedListener<String, String>
{

    private final EventSocket socket;

    public RoomMapEntryListener(EventSocket socket) {
        this.socket = socket;
    }

    @Override
    public void entryAdded( EntryEvent<String, String> event ) {
        System.out.println( "Entry Added:" + event );
    }

    @Override
    public void entryRemoved( EntryEvent<String, String> event ) {
        System.out.println( "Entry Removed:" + event );
    }

    @Override
    public void entryUpdated( EntryEvent<String, String> event ) {
        System.out.println( "Entry Updated:" + event );
        this.socket.sendRoomState();
    }

    @Override
    public void entryEvicted( EntryEvent<String, String> event ) {
        System.out.println( "Entry Evicted:" + event );
    }


}