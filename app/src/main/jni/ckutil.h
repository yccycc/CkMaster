
#ifndef CKMASTER_CKUTIL_H
#define CKMASTER_CKUTIL_H
int init_serial();
int uart_send(char *data, int datalen);
int uart_recv(char *data, int datalen);
void uart_close();

#endif //CKMASTER_CKUTIL_H
