import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FeatureProcurement } from './feature-procurement';

describe('FeatureProcurement', () => {
  let component: FeatureProcurement;
  let fixture: ComponentFixture<FeatureProcurement>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FeatureProcurement],
    }).compileComponents();

    fixture = TestBed.createComponent(FeatureProcurement);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
