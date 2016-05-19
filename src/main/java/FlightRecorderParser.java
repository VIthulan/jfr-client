import com.jrockit.mc.common.IMCFrame;
import com.jrockit.mc.common.IMCMethod;
import com.jrockit.mc.flightrecorder.FlightRecording;
import com.jrockit.mc.flightrecorder.FlightRecordingLoader;
import com.jrockit.mc.flightrecorder.internal.model.FLRStackTrace;
import com.jrockit.mc.flightrecorder.spi.IEvent;
import com.jrockit.mc.flightrecorder.spi.IEventType;
import com.jrockit.mc.flightrecorder.spi.IView;


import java.io.File;
import java.util.*;


public class FlightRecorderParser {
    final String EVENT_TYPE = "Method Profiling Sample";
    //final String FILE_PATH = "/home/vithulan/Documents/flight_recording_18091comintellijrtexecutionapplicationAppMainDemo18924.jfr";
    final String FILE_PATH = "/home/vithulan/IdeaProjects/jfr-test/src/main/java/myrecording.jfr";
   // FlightRecorderClient
    public void init() {
        Map<String,Integer> stackTraceMap = new LinkedHashMap<String, Integer>();
        FlightRecording recording = FlightRecordingLoader.loadFile(new File(FILE_PATH));
        IView view = recording.createView();
       // int count = 0;
        for(IEvent event : view){

           /* Iterator <String> iterator = event.getEventType().getFieldIdentifiers().iterator();
            while(iterator.hasNext()){
                System.out.println(iterator.next());
            }*/
            if(EVENT_TYPE.equals(event.getEventType().getName())){
                /*Iterator <String> iterator = event.getEventType().getFieldIdentifiers().iterator();
                while(iterator.hasNext()){
                    System.out.println(iterator.next());
                }*/
                FLRStackTrace flrStackTrace = (FLRStackTrace) event.getValue("(stackTrace)");
                Stack <String> stack = new Stack<String>();
                for(IMCFrame frame : flrStackTrace.getFrames()){
                    StringBuilder methodBuilder = new StringBuilder();
                    IMCMethod method = frame.getMethod();
                    methodBuilder.append(method.getHumanReadable(false,true,true,true,true,true));
                    methodBuilder.append(":");
                    methodBuilder.append(frame.getFrameLineNumber());

                    stack.push(methodBuilder.toString());
                }
                StringBuilder stackTraceBuilder = new StringBuilder();
                boolean appendSemicolon = false;
                while(!stack.empty()){
                    if(appendSemicolon){
                        stackTraceBuilder.append(";");
                    } else {
                        appendSemicolon = true;
                    }
                    stackTraceBuilder.append(stack.pop());
                }
                String stackStrace = stackTraceBuilder.toString();
                Integer count = stackTraceMap.get(stackStrace);
                if(count==null){
                    count = 1;
                }
                else {
                    count++;
                }
                stackTraceMap.put(stackStrace,count);
            }
            /*System.out.println(event.getEventType().getName());
            count++;*/
        }

        for(Map.Entry<String,Integer> entry : stackTraceMap.entrySet()){
            System.out.println(entry.getKey()+" "+entry.getValue());
        }
        //System.out.println(count+" Events!");
    }

}
