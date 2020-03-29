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

public class Hadoop_Q1 {

    public static class AverageMapper_Passenger extends Mapper<Object, Text, Text, IntWritable> {

        private final static IntWritable one = new IntWritable(1);
        private Text word = new Text();

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {

            //count words according to lines
            word.set(value);
            String line = word.toString();
            String[] col_vals = line.split(",");

            //initialize to calculate total number of passengers and trip
            Text total_Passengers_key = new Text("Total_Passenger");
            Text totTrip_key = new Text("Total_Trip");

            if (!col_vals[0].trim().isEmpty()) {
                DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                try {
                    // changing the type of first column to date value
                    LocalDateTime date = LocalDateTime.parse(col_vals[1], format);
                    System.out.println(date);

                    //store passenger key for total passengers
                    context.write(total_Passengers_key, new IntWritable(Integer.parseInt(col_vals[3])));

                    //store total trip keys in dataset
                    context.write(totTrip_key, new IntWritable(1));

                    //storing day week wise
                    DayOfWeek dow = date.getDayOfWeek();

                    // map passengers per day to total passenger key
                    Text daysKey = new Text(dow + "Passengers_Total");
                    context.write(daysKey, new IntWritable(Integer.parseInt(col_vals[3])));

                    // Each day total trip value
                    context.write(new Text(dow + "Trips_Total"), new IntWritable(1));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static class AverageCombiner_Passenger extends Reducer<Text, IntWritable, Text, IntWritable> {
        private IntWritable res = new IntWritable();

        //reducing the keys and values to count one by one
        public void reduce(Text key, Iterable<IntWritable> values, Context context)
                throws IOException, InterruptedException {
            int c = 0;
            for (IntWritable value : values) {
                c += value.get();
            }
            res.set(c);
            context.write(key, res);
        }
    }

        public static class AverageReducer_Passenger extends Reducer<Text, IntWritable, Text, DoubleWritable> {

        private IntWritable result = new IntWritable();
        double total_Passengers = 0.0;
        
        //reducing to sum up the keys and values of passengers and trips to calculate average
        public void reduce(Text key, Iterable<IntWritable> values, Context context)
                throws IOException, InterruptedException {
            int sum = 0;

            //for trip key value
            if(key.find("Trip")>0){
                for (IntWritable val : values){
                    sum+=val.get();
                }
            context.write(new Text(key+"_Avg"), new DoubleWritable(total_Passengers/sum));

            }
            //initially it will return passenger value aplabatically, so will start from here
            else {
                
            for (IntWritable val : values) {
                sum += val.get();
            }
                total_Passengers = sum;
        }
        }
    }
        public static void main(String[] args) throws Exception {
            //configuring environment for code to run
            Configuration conf = new Configuration();
            Job job = Job.getInstance(conf, "average passenger");
            job.setJarByClass(Hadoop_Q1.class);
            job.setMapperClass(AverageMapper_Passenger.class);
            job.setCombinerClass(AverageCombiner_Passenger.class);
            job.setReducerClass(AverageReducer_Passenger.class);
            job.setOutputKeyClass(Text.class);
            job.setOutputValueClass(IntWritable.class);
            FileInputFormat.addInputPath(job, new Path(args[0]));
            FileOutputFormat.setOutputPath(job, new Path(args[1]));
            System.exit(job.waitForCompletion(true) ? 0 : 1);
        }
}

