package com.pagerank;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class PageRankReducer extends Reducer<Text, Text, Text, Text> {
    private static final double DAMPING_FACTOR = 0.85;
    private static final double INITIAL_PAGERANK = 1.0;
    private static final int NODES_COUNT = 3;  // Change this if there are more nodes

    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        double sumContributions = 0.0;
        double currentPageRank = 0.0;

        // Process all values associated with this node
        for (Text val : values) {
            String value = val.toString();
            if (value.startsWith("CONTRIBUTION:")) {
                // This is a contribution from other nodes
                sumContributions += Double.parseDouble(value.split(":")[1]);
            } else if (value.startsWith("PAGERANK:")) {
                // This is the node's current PageRank (you may pass this from the previous iteration)
                currentPageRank = Double.parseDouble(value.split(":")[1]);
            }
        }

        // Compute the new PageRank
        double newPageRank = (1 - DAMPING_FACTOR) / NODES_COUNT + DAMPING_FACTOR * sumContributions;

        // Emit the new PageRank for the node
        context.write(key, new Text("PAGERANK:" + newPageRank));
    }
}
