if [ "$#" -ne 1 ]; then
   echo "Usage: # ./run.sh < {path_to_input_file}.csv"
   exit 1
fi

java -cp target/geocoder.jar com.lin.Geocoder $1
