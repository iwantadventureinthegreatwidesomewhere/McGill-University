SELECT l.type, count(*)
FROM Likes l, (SELECT u.userid
    FROM Users u, Messages m
    WHERE u.userid = m.userid
    GROUP BY u.userid
    HAVING count(*) > 5) mostActive
WHERE l.userid = mostActive.userid
GROUP BY l.type;