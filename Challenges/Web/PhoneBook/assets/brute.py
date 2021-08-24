import requests
from string import ascii_lowercase, ascii_uppercase

url = 'http://142.93.35.92:30472/login'


chars = ascii_lowercase+ascii_uppercase+'0123456789_{}()'

passwd = ''

success = requests.post

print(passwd)
