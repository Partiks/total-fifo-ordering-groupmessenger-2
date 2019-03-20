flag=1
count=0
while [ $flag -ne 0 ]
do
	echo "Successful Count = " $count
	/media/partiks/Study/586-DS/DS\ Android\ Projects/groupmessenger2-grading.linux ./app/build/outputs/apk/debug/app-debug.apk | tee /dev/tty >> shell_grader_output.txt
	tail -n 10 shell_grader_output.txt | grep -q -e "Total score: 0" -e "never sent" -e "Total score: 4"
	#echo "second line executed"
	#echo $grader_output 
	#echo "third line executed"
	#echo $grader_output 
	#echo "Total score: 0" | tee /dev/tty | egrep -q '(Total score: 0)'
	flag=$?
	echo "-------------------------------------------------------------------------------------------------"
	count=$((count+1))
done