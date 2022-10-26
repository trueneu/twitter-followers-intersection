# followers-intersection

A small program for fetching Twitter followers intersection (i.e. accounts that follow all of the specified) between 2 or more accounts. 

## Usage

- clone the repo
- install Leiningen (https://leiningen.org/)
- run the program:

```
$ lein run -- -h
  -o, --format FORMAT      :csv                          Output format: json|csv|yaml
  -u, --username USERNAME  []                            Usernames to lookup
  -c, --cache FILENAME     /home/trueneu/.flwrs-cache    Path to cache file
  -t, --token FILENAME     /home/trueneu/.twitter-token  Path to Twitter API bearer token file. File should contain the token only, no newline at the end
  -h, --help
```
You have to register for Twitter API v2 access here: https://developer.twitter.com/en/apply-for-access

Inject bearer token into the TOKEN file

Fire away:

```
$ lein run -- -u userID1 -u userID2 -o yaml
- username: <REDACTED>
  id: <REDACTED>
  followers_count: 0
  created_at: '2017-04-07T18:08:23.000Z'
- username: <REDACTED>
  id: <REDACTED>
  followers_count: 8
  created_at: '2011-05-07T18:47:42.000Z'
...
```

Followers data is cached for 7 days, as fetching the followers is heavily rate-limited (15 requests for 1000 followers per 15 minutes). For this reason, don't expect getting Elon Musk's followers list in less than a couple of days.


## License

Copyright © 2022 Pavel Gurkov

Distributed under MIT License.

See LICENSE for details.