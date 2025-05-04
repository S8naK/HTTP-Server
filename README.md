# Simple HTTP/1.1 server

Implementation of a simple HTTP server in Java.

## Features

- Handles simple GET and POST HTTP requests.
- Serves static HTML files (e.g., index.html).
- Can handle multiple concurrent connections, tested up to 10k.
- Basic HTTP/1.1 compliance.

### Access the server by navigating to http://localhost:8010 in your web browser.

## Performance Testing

- The server has been load-tested using Apache JMeter with 10,000 requests ramped up over 60 seconds, handling up to 60 concurrent threads.
- Performance improvements through multithreading resulted in 79% reduction in error rate (from 24.10% to 5.03%) and 3% increase in throughput (up to 165.6 requests/sec).
