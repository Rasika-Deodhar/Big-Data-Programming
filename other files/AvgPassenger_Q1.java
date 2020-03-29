import java.io.IOException;
import java.util.StringTokenizer;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.time.LocalDateTime;
import java.time.DayOfWeek;
import java.lang.Exception;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class AveragePassenger_Q1 {

    public static class AveragePassengerMapper extends Mapper<Object, Text, Text, IntWritable> {

        private final static IntWritable one = new IntWritable(1);
        private Text word = new Text();

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {

            word.set(value);
            String nextLine = word.toString();
            String[] columns = nextLine.split(",");

            Text totalPassengerkey = new Text("Total_Passenger");

            Text totalTripKey = new Text("Total_Trip");

            if (!columns[0].trim().isEmpty()) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                try {
                    // first column is converted to date
                    LocalDateTime date = LocalDateTime.parse(columns[1], formatter);
                    System.out.println(date);

                    // map number of passengers to key => Total_Passenger
                    // System.out.println(columns[3]);
                    context.write(totalPassengerkey, new IntWritable(Integer.parseInt(columns[3])));

                    // Total trips in the dataset
                    context.write(totalTripKey, new IntWritable(1));

                    // unique key for each day passenger
                    DayOfWeek dow = date.getDayOfWeek();

                    // map number of passengers for each day to key => MON_Total_Passenger,
                    // TUE_Total_Passenger
                    Text daysKey = new Text(dow + "_Total_Passenger");
                    context.write(daysKey, new IntWritable(Integer.parseInt(columns[3])));

                    // Total trips for each day
                    context.write(new Text(dow + "_Total_Trip"), new IntWritable(1));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Get intermediate passenger count for each key

    public static class AveragePassengerCombiner extends Reducer<Text, IntWritable, Text, IntWritable> {
        private IntWritable result = new IntWritable();

        public void reduce(Text key, Iterable<IntWritable> values, Context context)
                throws IOException, InterruptedException {
            int count = 0;
            for (IntWritable value : values) {
                count += value.get();
            }
            result.set(count);
            context.write(key, result);
        }
    }

        public static class AveragePassengerReducer extends Reducer<Text, IntWritable, Text, DoubleWritable> {

        private IntWritable result = new IntWritable();
        double totPassenger = 0.0;
        
        public void reduce(Text key, Iterable<IntWritable> values, Context context)
                throws IOException, InterruptedException {
            int sum = 0;
            if(key.find("Trip")>0){
                for (IntWritable val : values){
                    sum+=val.get();
                }
                // ext.write(key, new DoubleWritable(sum));   
            context.write(new Text(key+"_Avg"), new DoubleWritable(totPassenger/sum));

            }

            else {
                
            for (IntWritable val : values) {
                sum += val.get();
            }
                totPassenger = sum;
        }
            // context.write(key,new IntWritable(sum));
            // context.write(key, new IntWritable(count));
            // result.set(sum);
            // context.write(key, result);
        }
    }

        // remaining
        public static void main(String[] args) throws Exception {
            Configuration conf = new Configuration();
            Job job = Job.getInstance(conf, "average passenger");
            job.setJarByClass(AveragePassenger_Q1.class);
            job.setMapperClass(AveragePassengerMapper.class);
            job.setCombinerClass(AveragePassengerCombiner.class);
            job.setReducerClass(AveragePassengerReducer.class);
            job.setOutputKeyClass(Text.class);
            job.setOutputValueClass(IntWritable.class);
            FileInputFormat.addInputPath(job, new Path(args[0]));
            FileOutputFormat.setOutputPath(job, new Path(args[1]));
            System.exit(job.waitForCompletion(true) ? 0 : 1);
        }
}

