import { Injectable } from '@angular/core';
import { TraderInfo } from './trader-info';
import { Http } from '@angular/http';
import 'rxjs/add/operator/toPromise';

@Injectable()
export class TraderStatusService {

  constructor(private http: Http) { }

  getTraderInfo(): Promise<TraderInfo> {


    return  this.http.get('http://localhost:8100/info/test').toPromise()
             .then(response => { console.log('response from server: ' + response.json());
               return response.json() as TraderInfo;
             })
             .catch(this.handleError);
  }

private handleError(error: any): Promise<any> {
  console.error('An error occurred', error); // for demo purposes only
  return Promise.reject(error.message || error);
}

}
