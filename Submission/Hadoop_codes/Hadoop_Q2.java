import java.io.IOException;
import java.util.StringTokenizer;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.time.LocalDateTime;
import java.time.DayOfWeek;
import java.lang.Exception;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class Hadoop_Q2 {

    public static class AverageMapper_TripDistance extends Mapper<Object, Text, Text, DoubleWritable> {

        private final static DoubleWritable one = new DoubleWritable(1);
        private Text word = new Text();

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {

            //count each word line by line by delimiter ','
            word.set(value);
            String line = word.toString();
            String[] col_vals = line.split(",");

            Text total_Passengerskey = new Text("Total_Distance");

            Text totTripKey = new Text("Total_Trip");

            if (!col_vals[0].trim().isEmpty()) {
                DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                try {
                    // changing the type of first column to date value
                    LocalDateTime date = LocalDateTime.parse(col_vals[1], format);
                    System.out.println(date);

                    //store passenger key for total passengers
                    context.write(total_Passengerskey, new DoubleWritable(Float.parseFloat(col_vals[4])));

                    // unique key for each day passenger
                    DayOfWeek days_of_week = date.getDayOfWeek();

                    // store distance key for each day
                    Text daysKey = new Text(days_of_week + "_Total_Distance");
                    context.write(daysKey, new DoubleWritable(Float.parseFloat(col_vals[4])));

                    // store total trips done each day
                    context.write(new Text(days_of_week + "_Total_Trip"), one);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Get passenger count for each key
    public static class AverageCombiner_TripDistance extends Reducer<Text, DoubleWritable, Text, DoubleWritable> {
        private DoubleWritable result = new DoubleWritable();

        //reducing the keys and values to count one by one
        public void reduce(Text key, Iterable<DoubleWritable> values, Context context)
                throws IOException, InterruptedException {
            float count = 0;
            for (DoubleWritable value : values) {
                count += value.get();
            }
            result.set(count);
            context.write(key, result);
        }
    }

        public static class AverageReducer_TripDistance extends Reducer<Text, DoubleWritable, Text, DoubleWritable> {

        private DoubleWritable result = new DoubleWritable();
        float total_Passengers = 0;
        
        //reducing to sum up the keys and values of passengers and trips to calculate average
        public void reduce(Text key, Iterable<DoubleWritable> values, Context context)
                throws IOException, InterruptedException {
            float sum = 0;
            //calculating sum of trips made
            if(key.find("Trip")>0){
                for (DoubleWritable val : values){
                    sum+=val.get();
                }
                 
            context.write(new Text(key+"_Avg"), new DoubleWritable(total_Passengers/sum));

            }

            else {
                //adding number of passengers -> this value would come first so would get calculated first
            for (DoubleWritable val : values) {
                sum += val.get();
            }
                total_Passengers = sum;
        }
        }
    }
        public static void main(String[] args) throws Exception {
            //configuring environment for code to run
            Configuration conf = new Configuration();
            Job job = Job.getInstance(conf, "average trip distance");
            job.setJarByClass(Hadoop_Q2.class);
            job.setMapperClass(AverageMapper_TripDistance.class);
            job.setCombinerClass(AverageCombiner_TripDistance.class);
            job.setReducerClass(AverageReducer_TripDistance.class);
            job.setOutputKeyClass(Text.class);
            job.setOutputValueClass(DoubleWritable.class);
            FileInputFormat.addInputPath(job, new Path(args[0]));
            FileOutputFormat.setOutputPath(job, new Path(args[1]));
            System.exit(job.waitForCompletion(true) ? 0 : 1);
        }
}
