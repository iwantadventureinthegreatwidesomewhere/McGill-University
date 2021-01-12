SELECT EXTRACT(month FROM (timestamp)), count(*)
FROM Messages
GROUP BY EXTRACT(month FROM (timestamp))
ORDER BY EXTRACT(month FROM (timestamp));