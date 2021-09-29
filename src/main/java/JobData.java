import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by LaunchCode
 */
public class JobData {

    private static final String DATA_FILE = "src/main/resources/job_data.csv"; // path to the source of the job data
    private static boolean isDataLoaded = false; // true after the jobs data is loaded into the list of jobs

    private static ArrayList<HashMap<String, String>> allJobs; // will hold full list of jobs after that data is loaded

    /**
     * Fetch list of all values from loaded data,
     * without duplicates, for a given column.
     *
     * @param field The column to retrieve values from
     * @return List of all of the values of the given field
     */
    public static ArrayList<String> findAll(String field) {

        // load data, if not already loaded
        loadData();

        ArrayList<String> values = new ArrayList<>(); // will hold a temp list of unique volues for the column

        // for each job in the jobs list, get each value for the column...
        for (HashMap<String, String> row : allJobs) {
            String aValue = row.get(field);
            //...if the current value for the column isn't already in the list,
            // then add it to the temporary list of unique values for the column
            if (!values.contains(aValue)) {
                values.add(aValue);
            }
        }

        // Bonus mission: sort the results
        // the Collections.sort method sorts the Strings in the ArrayList in ascending order
        Collections.sort(values);

        return values; // return the temp list of unique values for the column
    }


    /**
     * Fetch list of all columns and values for all jobs from loaded data
     *
     *
     * @return copy of the list of all the job data that was loaded
     */
    public static ArrayList<HashMap<String, String>> findAll() {

        // load data, if not already loaded
        loadData();

        // Bonus mission; normal version returns allJobs

        // create an empty arraylist to hold a copy of the global list containing loaded jobs data
        ArrayList<HashMap<String, String>> allJobsCopy = new ArrayList<>();

        // for each job in the global list of loaded jobs data....
        for (HashMap<String, String> row : allJobs) {
            // Create an empty HashMap
            HashMap<String, String> myMap = new HashMap<>();
            // for each column in the current row, add the key (column) / value pairs for the job to the hashmap
            for (String column : row.keySet()) {
                myMap.put(column, row.get(column));
            }
            // add the temporary HashMap to the temporary list of all jobs
            allJobsCopy.add(myMap);
        }

        return allJobsCopy; // return the copy of the allJobs list
        // return new ArrayList<>(allJobs);
    }

    /**
     * Returns results of search the jobs data by key/value, using
     * inclusion of the search term.
     *
     * For example, searching for employer "Enterprise" will include results
     * with "Enterprise Holdings, Inc".
     *
     * @param column   Column that should be searched.
     * @param value Value of the field to search for
     * @return List of all jobs matching the criteria
     */
    public static ArrayList<HashMap<String, String>> findByColumnAndValue(String column, String value) {

        // load data, if not already loaded
        loadData();

        value = value.toLowerCase();
        ArrayList<HashMap<String, String>> jobs = new ArrayList<>();

        /* for each job in the full jobs list, loop through and
           check if the column value contains the search term */
        for (HashMap<String, String> row : allJobs) {

            String aValue = row.get(column);
            /* for the current job, if the column value (caseinsensitive) contains the search term,
               add the job to the temp jobs list. */
            if (aValue.toLowerCase().contains(value)) {
                jobs.add(row);
            }
        }

        return jobs; // return the temp jobs list
    }

    /**
     * Search all columns for the given term
     *
     * @param value The search term to look for
     * @return      List of all jobs with at least one field containing the value
     */
    public static ArrayList<HashMap<String, String>> findByValue(String value) {

        // load data, if not already loaded
        loadData();

        // TODO - implement this method
        value = value.toLowerCase();  // to ensure case insensitivity of search term
        ArrayList<HashMap<String, String>> jobs = new ArrayList<>(); /* temp jobs list will hold any jobs
                                                                        that contain the search term */
        /* for each job in the full jobs list, loop through and
           check if any of the column values contain the search term */
        for (HashMap<String, String> row : allJobs) {
            for (String currentColumn : row.keySet()) {
                /* for the current job, if the column value (case insensitive) contains the search term,
                   add the job to the temp jobs list. */
                if (row.get(currentColumn).toLowerCase().contains(value)) {
                    jobs.add(row);
                    break; /* search term was found for a column, so move to the next job now
                            (to prevent duplicates of the current job in the temp jobs list
                            if more than one column contains the search key) */
                }
            }
        }

        return jobs; // return the temp jobs list
    }

    /**
     * Read in data from a CSV file and store it in a list
     */
    private static void loadData() {

        // Only load data once
        if (isDataLoaded) {
            return;
        }

        try {

            // Open the CSV file and set up pull out column header info and records
            Reader in = new FileReader(DATA_FILE);
            CSVParser parser = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);
            List<CSVRecord> records = parser.getRecords();
            Integer numberOfColumns = records.get(0).size();
            String[] headers = parser.getHeaderMap().keySet().toArray(new String[numberOfColumns]);

            allJobs = new ArrayList<>();

            // Put the records into a more friendly format
            for (CSVRecord record : records) {
                HashMap<String, String> newJob = new HashMap<>();

                for (String headerLabel : headers) {
                    newJob.put(headerLabel, record.get(headerLabel));
                }

                allJobs.add(newJob);
            }

            // flag the data as loaded, so we don't do it twice
            isDataLoaded = true;

        } catch (IOException e) {
            System.out.println("Failed to load job data");
            e.printStackTrace();
        }
    }

}
