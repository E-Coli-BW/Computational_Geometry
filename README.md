# Computational_Geometry
Computational Geometry Course Assignments Code Repo
Repo includes codes for Assignments shown below:
# 1. ConvexHull: 
Three algorithms for finding Convex Hull
  1) Slow: O(n^3) Simple Brute Force Test;
  2) Medium: O(n^2) 
  3) Fast: O(nlogn) First order the points by X-coordinate, then check for validity in linear time;
# 2. Segments:
Find intersection points of a list of Segments you draw in the window
Use Sweepline algorithm

-To find the intersections of Segments, we use a Sweepline to sweep through all the lines drawn by the user. 
 1. For each event our Sweepline encountered, check for possible intersections according to the type of event Sweepline encountered.
 2. The check function itself is using AreaABC.sign() to check for intersections;
 3. If two lines intersects, we have method called ABintersectsCD to calculate the intersection point;
-To handle multiple lines scenarios, we use SweepList to **record** and **order** the lines we encounters
-The ordering is done by the **compareTo** method
-The sweepline can encounter three different points (which we call events, and we store events in EventList which is initally empty, note that once our EventList is empty again, that means all the operations are done and if our implementation is correct, the program should end and all intersections been found out fully and correctly)
## 1).Tail of a segment 
  0. If we have Tail events, that means we are seeing the segment for the first time, add the Segments to EventList
  1. As we add a new segment into the EventList, check againsts its neighbors to see if we have new intersections occurring
## 2).Head of a segment;
  0. If we have Head events, that means for that segment, we have seen to its end
  1. As we have seen the entire segment, we can remove it from the SweepList because we are done with everything we can do about it. **BUT**, before we remove it, we need to check its Previous Segment with its Next Segment as these two would become new neibors once we remove the segment. However, if we don't do this check before removing the Segment, we will never be able to find our s.getPrevious() and s.getNext() (s doesn't exists anymore!)
## 3).Intersections of two segments;
  0. If we have Intersection events, that means, once we pass this point, the ordering of the lines are gonna change. (the compareTo method is ordering the lines from left to right based on the order Segments intersects with the SweepLine, so after passing the intersection, the order of the two segments we are examining are actually swapped! (draw two intersecting segments and a Sweepline passing the intersection points to see for yourself why this is happening)
  1. Note that in this case, we don't need to add or remove Segments to SweepList because we have seen both lines, we have not seen the entirety of either of the two lines, so no removals. The only thing happens is the swap of the Nodes(Notes 1 below explains the relationship between Nodes and Segment)
  
**Notes 1**: We have Nodes and Segment objects, each Node is pointing to a Segment and each Segment is also pointing to a Node, so Node can find Segments, Segments can also find Nodes. 

# 3. Polygon:
Find Complement and Union of two Polygons
After completing Complement and Union method, Difference, Intersection can be done using operations of Complement and Union
-Union method Implementation
=======
# Computational_Geometry
Computational Geometry Course Assignments Code Repo
Repo includes codes for Assignments shown below:
# 1. ConvexHull: 
Three algorithms for finding Convex Hull
  1) Slow: O(n^3) Simple Brute Force Test;
  2) Medium: O(n^2) 
  3) Fast: O(nlogn) First order the points by X-coordinate, then check for validity in linear time;
# 2. Segments:
Find intersection points of a list of Segments you draw in the window
Use Sweepline algorithm

-To find the intersections of Segments, we use a Sweepline to sweep through all the lines drawn by the user. 
 1. For each event our Sweepline encountered, check for possible intersections according to the type of event Sweepline encountered.
 2. The check function itself is using AreaABC.sign() to check for intersections;
 3. If two lines intersects, we have method called ABintersectsCD to calculate the intersection point;
-To handle multiple lines scenarios, we use SweepList to **record** and **order** the lines we encounters
-The ordering is done by the **compareTo** method
-The sweepline can encounter three different points (which we call events, and we store events in EventList which is initally empty, note that once our EventList is empty again, that means all the operations are done and if our implementation is correct, the program should end and all intersections been found out fully and correctly)
## 1).Tail of a segment 
  0. If we have Tail events, that means we are seeing the segment for the first time, add the Segments to EventList
  1. As we add a new segment into the EventList, check againsts its neighbors to see if we have new intersections occurring
## 2).Head of a segment;
  0. If we have Head events, that means for that segment, we have seen to its end
  1. As we have seen the entire segment, we can remove it from the SweepList because we are done with everything we can do about it. **BUT**, before we remove it, we need to check its Previous Segment with its Next Segment as these two would become new neibors once we remove the segment. However, if we don't do this check before removing the Segment, we will never be able to find our s.getPrevious() and s.getNext() (s doesn't exists anymore!)
## 3).Intersections of two segments;
  0. If we have Intersection events, that means, once we pass this point, the ordering of the lines are gonna change. (the compareTo method is ordering the lines from left to right based on the order Segments intersects with the SweepLine, so after passing the intersection, the order of the two segments we are examining are actually swapped! (draw two intersecting segments and a Sweepline passing the intersection points to see for yourself why this is happening)
  1. Note that in this case, we don't need to add or remove Segments to SweepList because we have seen both lines, we have not seen the entirety of either of the two lines, so no removals. The only thing happens is the swap of the Nodes(Notes 1 below explains the relationship between Nodes and Segment)
  
**Notes 1**: We have Nodes and Segment objects, each Node is pointing to a Segment and each Segment is also pointing to a Node, so Node can find Segments, Segments can also find Nodes. 

# 3. Polygon:
Find Complement and Union of two Polygons
After completing Complement and Union method, Difference, Intersection can be done using operations of Complement and Union
-Union method Implementation
