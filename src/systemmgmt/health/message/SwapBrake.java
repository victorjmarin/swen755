package systemmgmt.health.message;

import io.mappedbus.MappedBusMessage;
import io.mappedbus.MemoryMappedFile;

public class SwapBrake implements MappedBusMessage {

	public SwapBrake(){
		
	}
	@Override
	public void write(MemoryMappedFile mem, long pos) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void read(MemoryMappedFile mem, long pos) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int type() {
		// TODO Auto-generated method stub
		return 3;
	}
	
}
