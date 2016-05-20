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
    Map <String,Long> timeCounter = new HashMap<String, Long>();
    public void init() {
        Map<String,Integer> stackTraceMap = new LinkedHashMap<String, Integer>();
        FlightRecording recording = FlightRecordingLoader.loadFile(new File(FILE_PATH));
        IView view = recording.createView();
       // int count = 0;
        int c = 1;

        for(IEvent event : view){

           // System.out.println("===================  "+c+"  ==================");
           /* Iterator <String> iterator = event.getEventType().getFieldIdentifiers().iterator();
            while(iterator.hasNext()){
                System.out.println(iterator.next());
            }*/
//            if(EVENT_TYPE.equals(event.getEventType().getName())){
            if(true) {
                /*Iterator <String> iterator = event.getEventType().getFieldIdentifiers().iterator();
                while(iterator.hasNext()){
                    System.out.println(iterator.next());
                }*/
                FLRStackTrace flrStackTrace = (FLRStackTrace) event.getValue("(stackTrace)");
                Stack<String> stack = new Stack<String>();
                if (flrStackTrace != null) {
                    for (IMCFrame frame : flrStackTrace.getFrames()) {
                        StringBuilder methodBuilder = new StringBuilder();
                        IMCMethod method = frame.getMethod();
                        methodBuilder.append(method.getHumanReadable(false, true, true, true, true, true));
                        if(timeCounter.get(methodBuilder.toString())==null){
                            timeCounter.put(methodBuilder.toString(),event.getDuration());
                        }
                        else {
                            Long time = timeCounter.get(methodBuilder.toString());
                            time = time + event.getDuration();
                            timeCounter.put(methodBuilder.toString(),time);
                        }
                        methodBuilder.append(":");
                        methodBuilder.append(event.getEventType().getName());
                        //methodBuilder.append(frame.getFrameLineNumber());
                        /*System.out.println();
                        System.out.println(methodBuilder.toString()+"  ===  "+event.getEventType().getName());
                        System.out.println("Duration "+event.getDuration());
                        System.out.println();*/
                        stack.push(methodBuilder.toString());

                    }
                    StringBuilder stackTraceBuilder = new StringBuilder();
                    boolean appendSemicolon = false;
                    while (!stack.empty()) {
                        if (appendSemicolon) {
                            stackTraceBuilder.append(";");
                        } else {
                            appendSemicolon = true;
                        }
                        stackTraceBuilder.append(stack.pop());
                    }
                    String stackStrace = stackTraceBuilder.toString();
                    Integer count = stackTraceMap.get(stackStrace);
                    if (count == null) {
                        count = 1;
                    } else {
                        count++;
                    }
                    stackTraceMap.put(stackStrace, count);
                }
            }
            /*System.out.println(event.getEventType().getName());
            count++;*/
          //  c++;
        }
        System.out.println();
        System.out.println("======================All Stack traces========================");
        System.out.println();
        for(Map.Entry<String,Integer> entry : stackTraceMap.entrySet()){
            System.out.println(entry.getKey()+" "+entry.getValue());
        }
        System.out.println();
        System.out.println();
        System.out.println("====================== Hot methods with time taken ========================");
        System.out.println();
        LinkedHashMap<String,Long> orderedMap = sortbyTime(timeCounter);
        for(Map.Entry<String,Long> entry : orderedMap.entrySet()){
            System.out.println(entry.getKey()+" : "+entry.getValue()+" ns");
        }
    }

    public LinkedHashMap<String,Long> sortbyTime (Map<String,Long> unorderedMap){
        List<Map.Entry<String,Long>> list =
                new LinkedList<Map.Entry<String, Long>>(unorderedMap.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Long>>() {
            public int compare(Map.Entry<String, Long> o1, Map.Entry<String, Long> o2) {
                return (o2.getValue().compareTo(o1.getValue()));
            }
        });

        LinkedHashMap <String,Long> ordered = new LinkedHashMap<String, Long>();
        for (Map.Entry<String,Long> entry : list){
            ordered.put(entry.getKey(),entry.getValue());
        }

        return ordered;
    }

}
