import java.io.IOException;
import java.util.*;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.util.*;

public class count extends Configured implements Tool{

  public static class MapClass extends MapReduceBase implements Mapper<Text, Text, Text, Text> {
    public void map(Text key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException{
      output.collect(value, key);
    }
  }

  public static class Reduce extends MapReduceBase implements Reducer<Text, Text, Text, IntWritable>{
    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text,IntWritable> output, Reporter reporter) throws IOException{
      int count = 0;
      while(values.hasNext()){
        count ++;
        values.next();
      }
      output.collect(key, new IntWritable(count));
    }
  }

  public int run(String[] args) throws Exception{
    Configuration conf = getConf();

    JobConf job = new JobConf(conf, count.class);
    
    Path in = new Path(args[0]);
    Path out = new Path(args[1]);
    FileInputFormat.setInputPaths(job, in);
    FileOutputFormat.setOutputPath(job, out);

    job.setJobName("count");
    job.setMapperClass(MapClass.class);
    job.setReducerClass(Reduce.class);
    
    job.setInputFormat(KeyValueTextInputFormat.class);
    job.setOutputFormat(TextOutputFormat.class);
    job.setOutputKeyClass(Text.class); 
    job.setOutputValueClass(Text.class); 
    job.set("key.value.separator.in.input.line", ",");

    JobClient.runJob(job);
    return 0;
  }

  public static void main(String[] args) throws Exception{
    int res = ToolRunner.run(new Configuration(), new count(), args);

    System.exit(res);
  }
}
